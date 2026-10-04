package com.pinodesk.repository;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.function.Consumer;
import javax.sql.DataSource;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import com.pinodesk.constant.UserGroupStatus;
import com.pinodesk.entity.Supplier;
import com.pinodesk.entity.UserGroup;
import com.pinodesk.viewmodel.SupplierFilterVM;
import com.pinodesk.viewmodel.UserGroupFilterVM;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SupplierAndGroupQueryContractTest extends RepositoryTestBase {
    @Autowired
    private DataSource dataSource;
    @Autowired
    private SupplierRepository suppliers;
    @Autowired
    private UserGroupRepository groups;
    private JdbcTemplate jdbc;

    @BeforeAll
    void schemaFromProductionMigrations() {
        new ResourceDatabasePopulator(
                new ClassPathResource("db/migration/V0012__create_table_supplier.sql"),
                new ClassPathResource("db/migration/V0002__create_table_user_group.sql")).execute(dataSource);
        jdbc = new JdbcTemplate(dataSource);
    }

    @BeforeEach
    void fixtures() {
        jdbc.update(
                "insert into supplier(id, code, name, phone, email, website, address) values (9501,'SUP-A','Alpha Medical','08111','alpha@example.com','alpha.test','North Road'),(9502,'SUP-B','Beta Tools','08222','beta@example.org','beta.test','South Road')");
        jdbc.update(
                "insert into supplier(id, code, name, deleted_at) values(9503,'SUP-C','Alpha Deleted', current_timestamp)");
        jdbc.update(
                "insert into user_group(id,name,description,status) values(9601,'Cashier','Sales desk','active'),(9602,'Warehouse','Stock room','inactive')");
        jdbc.update(
                "insert into user_group(id,name,description,status,deleted_at) values(9603,'Cashier Deleted','Sales desk','active',current_timestamp)");
    }

    @AfterEach
    void cleanup() {
        jdbc.update("delete from supplier where id between 9501 and 9503");
        jdbc.update("delete from user_group where id between 9601 and 9603");
    }

    private List<Long> supplierIds(List<Supplier> rows) {
        return rows.stream().map(Supplier::getId).sorted().toList();
    }

    private List<Long> groupIds(List<UserGroup> rows) {
        return rows.stream().map(UserGroup::getId).sorted().toList();
    }

    private void supplierMatches(Consumer<SupplierFilterVM> configure, Long... expected) {
        SupplierFilterVM f = new SupplierFilterVM();
        configure.accept(f);
        assertEquals(List.of(expected), supplierIds(suppliers.findByFilter(f)));
    }

    @Test
    void supplierFiltersCoverEveryFieldAndExcludeDeletedRows() {
        supplierMatches(f -> {
        }, 9501L, 9502L);
        supplierMatches(f -> f.setName("ALPHA"), 9501L);
        supplierMatches(f -> f.setCode("SUP-A"), 9501L);
        supplierMatches(f -> f.setPhone("081"), 9501L);
        supplierMatches(f -> f.setEmail("ALPHA@"), 9501L);
        supplierMatches(f -> f.setWebsite("ALPHA.TEST"), 9501L);
        supplierMatches(f -> f.setAddress("NORTH"), 9501L);
        supplierMatches(f -> {
            f.setName("alpha");
            f.setCode("SUP-A");
            f.setPhone("081");
            f.setEmail("example.com");
            f.setWebsite("alpha");
            f.setAddress("north");
        }, 9501L);
        supplierMatches(f -> {
            f.setName("alpha");
            f.setAddress("south");
        });
        supplierMatches(f -> {
            f.setName(" ");
            f.setCode("");
            f.setPhone(" ");
            f.setEmail("");
            f.setWebsite(" ");
            f.setAddress("");
        }, 9501L, 9502L);
    }

    @Test
    void supplierKeywordSearchUsesOrAcrossAllFields() {
        for (String term : List.of("Medical", "SUP-A", "08111", "alpha@example.com", "alpha.test", "North"))
            assertEquals(List.of(9501L), supplierIds(suppliers.findByKeyword(term)), term);
        assertEquals(List.of(), supplierIds(suppliers.findByKeyword("Deleted")));
        assertEquals(List.of(9501L, 9502L), supplierIds(suppliers.findByKeyword("")));
    }

    @Test
    void supplierDerivedQueriesIgnoreDeletedRecordsAndMatchCaseInsensitively() {
        assertTrue(suppliers.existsByCodeIgnoreCaseAndDeletedAtIsNull("sup-a"));
        assertFalse(suppliers.existsByCodeIgnoreCaseAndDeletedAtIsNull("SUP-C"));
        assertTrue(suppliers.existsByEmailIgnoreCaseAndDeletedAtIsNull("ALPHA@EXAMPLE.COM"));
        assertTrue(suppliers.existsByPhoneIgnoreCaseAndDeletedAtIsNull("08111"));
        assertEquals(List.of(9501L, 9502L), supplierIds(suppliers.findByDeletedAtIsNull()));
        assertTrue(suppliers.findByIdAndDeletedAtIsNull(9503L).isEmpty());
        assertEquals(9502L, suppliers.findFirstByCodeStartingWithOrderByCodeDesc("SUP-B").orElseThrow().getId());
    }

    @Test
    void supplierSoftDeletePersistsTimestampAndChangesSubsequentSearches() {
        assertEquals(1L, suppliers.deleteUpdateByIdIn(List.of(9501L)));
        assertTrue(suppliers.findByIdAndDeletedAtIsNull(9501L).isEmpty());
        assertNotNull(jdbc.queryForObject("select deleted_at from supplier where id=9501", java.sql.Timestamp.class));
        supplierMatches(f -> {
        }, 9502L);
    }

    @Test
    void groupFiltersCombineTextAndStatusAndExcludeDeletedRecords() {
        UserGroupFilterVM f = new UserGroupFilterVM();
        f.setName("CASH");
        assertEquals(List.of(9601L), groupIds(groups.findByFilter(f)));
        f.setName(null);
        f.setDescription("STOCK");
        assertEquals(List.of(9602L), groupIds(groups.findByFilter(f)));
        f.setStatus(UserGroupStatus.INACTIVE);
        assertEquals(List.of(9602L), groupIds(groups.findByFilter(f)));
        f.setStatus(UserGroupStatus.ACTIVE);
        assertEquals(List.of(), groupIds(groups.findByFilter(f)));
        f.setName(" ");
        f.setDescription("");
        f.setStatus(null);
        assertEquals(List.of(1L, 9601L, 9602L), groupIds(groups.findByFilter(f)));
    }

    @Test
    void groupKeywordSearchMatchesNameDescriptionAndStatus() {
        assertEquals(List.of(9601L), groupIds(groups.findByKeyword("CASH")));
        assertEquals(List.of(9602L), groupIds(groups.findByKeyword("STOCK")));
        assertEquals(List.of(9602L), groupIds(groups.findByKeyword("INACTIVE")));
        assertEquals(List.of(), groupIds(groups.findByKeyword("Deleted")));
    }
}
