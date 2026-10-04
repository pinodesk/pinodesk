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
import com.pinodesk.viewmodel.DoctorFilterVM;
import com.pinodesk.viewmodel.DoctorVM;
import com.pinodesk.viewmodel.DoctorCategoryVM;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DoctorQueryContractTest extends RepositoryTestBase {
    @Autowired
    private DataSource dataSource;
    @Autowired
    private DoctorRepository doctors;
    @Autowired
    private DoctorCategoryRepository categories;
    private JdbcTemplate jdbc;

    @BeforeAll
    void schema() {
        new ResourceDatabasePopulator(
                new ClassPathResource("db/migration/V0020__create_and_init_table_doctor.sql"),
                new ClassPathResource("db/migration/V0026__alter_table_doctor_add_columns.sql")).execute(dataSource);
        jdbc = new JdbcTemplate(dataSource);
    }

    @BeforeEach
    void fixture() {
        jdbc.update(
                "insert into doctor(id,code,category_code,name,registration_number,medical_license_number,phone,email,address) values(9701,'DOC-A','0001','Alpha Medic','REG-A','LIC-A','08111','alpha@example.com','North Road'),(9702,'DOC-B','0002','Beta Dentist','REG-B','LIC-B','08222','beta@example.org','South Road')");
        jdbc.update(
                "insert into doctor(id,code,category_code,name,deleted_at) values(9703,'DOC-C','0001','Deleted Medic',current_timestamp)");
    }

    @AfterEach
    void cleanup() {
        jdbc.update("delete from doctor where id between 9701 and 9703");
    }

    private List<Long> ids(List<DoctorVM> rows) {
        return rows.stream().map(DoctorVM::getId).sorted().toList();
    }

    private void matches(Consumer<DoctorFilterVM> setup, Long... expected) {
        DoctorFilterVM f = new DoctorFilterVM();
        setup.accept(f);
        assertEquals(List.of(expected), ids(doctors.findByFilter(f, "en")));
    }

    @Test
    void filtersMatchEachFieldAndCombineWithAnd() {
        matches(f -> {
        }, 9701L, 9702L);
        matches(f -> f.setName("ALPHA"), 9701L);
        matches(f -> f.setCode("DOC-A"), 9701L);
        matches(f -> f.setRegistrationNumber("reg-a"), 9701L);
        matches(f -> f.setMedicalLicenseNumber("lic-a"), 9701L);
        matches(f -> f.setPhone("081"), 9701L);
        matches(f -> f.setEmail("ALPHA@"), 9701L);
        matches(f -> f.setAddress("NORTH"), 9701L);
        matches(f -> {
            f.setName("alpha");
            f.setAddress("south");
        });
        matches(f -> {
            f.setName(" ");
            f.setCode("");
            f.setRegistrationNumber(" ");
            f.setMedicalLicenseNumber("");
            f.setPhone(" ");
            f.setEmail("");
            f.setAddress(" ");
        }, 9701L, 9702L);
    }

    @Test
    void categoryFilterUsesLocalizedCategoryId() {
        Long id = jdbc.queryForObject("select id from doctor_category where code='0001' and language='en'", Long.class);
        matches(f -> {
            DoctorCategoryVM c = new DoctorCategoryVM();
            c.setId(id);
            f.setCategory(c);
        }, 9701L);
        List<DoctorVM> localized = doctors.findByFilter(new DoctorFilterVM(), "id");
        assertEquals(List.of(9701L, 9702L), ids(localized));
        assertEquals(
                "Dokter Umum",
                localized.stream().filter(d -> d.getId() == 9701L).findFirst().orElseThrow().getCategoryName());
        assertEquals(List.of(), doctors.findByFilter(new DoctorFilterVM(), "xx"));
    }

    @Test
    void keywordSearchFindsEachContactFieldAndHandlesBlankInput() {
        for (String term : List.of("ALPHA", "08111", "alpha@example.com", "NORTH", "0001"))
            assertEquals(List.of(9701L), ids(doctors.findByKeyword(" " + term + " ", "en")), term);
        assertEquals(List.of(9701L, 9702L), ids(doctors.findByKeyword(" ", "en")));
        assertEquals(List.of(), doctors.findByKeyword("Deleted", "en"));
    }

    @Test
    void categoryQueriesRestrictLanguageAndMatchNameOrCode() {
        assertEquals("0001", categories.findByKeyword("PRACTITIONER", "en").get(0).getCode());
        assertEquals("Dokter Umum", categories.findByKeyword("0001", "id").get(0).getName());
        assertTrue(categories.findByKeyword("0001", "xx").isEmpty());
    }

    @Test
    void derivedUniquenessAndSoftDeleteQueriesRespectActiveRecords() {
        assertTrue(doctors.existsByCodeIgnoreCaseAndDeletedAtIsNull("doc-a"));
        assertFalse(doctors.existsByCodeIgnoreCaseAndDeletedAtIsNull("DOC-C"));
        assertTrue(doctors.existsByRegistrationNumberAndDeletedAtIsNull("REG-A"));
        assertTrue(doctors.existsByMedicalLicenseNumberAndDeletedAtIsNull("LIC-A"));
        assertTrue(doctors.existsByEmailIgnoreCaseAndDeletedAtIsNull("ALPHA@EXAMPLE.COM"));
        assertTrue(doctors.existsByPhoneAndDeletedAtIsNull("08111"));
        doctors.deleteUpdateByIdIn(List.of(9701L));
        assertTrue(doctors.findByIdAndDeletedAtIsNull(9701L).isEmpty());
        matches(f -> {
        }, 9702L);
    }
}
