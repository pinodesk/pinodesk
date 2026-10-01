package com.pinodesk.repository;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.pinodesk.entity.Customer;
import com.pinodesk.viewmodel.CustomerFilterVM;

@DatabaseSetup("CustomerRepositoryTest.xml")
class CustomerQueryContractTest extends RepositoryTestBase {
    @Autowired
    private CustomerRepository customers;

    private List<Long> ids(List<Customer> results) {
        return results.stream().map(Customer::getId).sorted().collect(Collectors.toList());
    }

    @Test
    void keywordMatchesEverySupportedColumn() {
        for (String keyword : List.of("MUHAM", "202104010001", "909001", "MUHAMMAD@", "JAKARTA")) {
            assertEquals(List.of(1L), ids(customers.findByKeyword(keyword)), keyword);
        }
        assertTrue(customers.findByKeyword("does-not-exist").isEmpty());
    }

    @Test
    void eachFilterCanBeUsedIndependentlyAndBlankFiltersAreIgnored() {
        CustomerFilterVM filter = new CustomerFilterVM();
        filter.setName("MUHAM");
        assertEquals(List.of(1L), ids(customers.findByFilter(filter)));
        filter = new CustomerFilterVM();
        filter.setCode("0001");
        assertEquals(List.of(1L), ids(customers.findByFilter(filter)));
        filter = new CustomerFilterVM();
        filter.setPhone("9001");
        assertEquals(List.of(1L), ids(customers.findByFilter(filter)));
        filter = new CustomerFilterVM();
        filter.setEmail("MUHAMMAD@");
        assertEquals(List.of(1L), ids(customers.findByFilter(filter)));
        filter = new CustomerFilterVM();
        filter.setAddress("JAKARTA");
        assertEquals(List.of(1L), ids(customers.findByFilter(filter)));
        filter.setName(" \t");
        filter.setCode(" ");
        filter.setPhone("");
        filter.setEmail(" ");
        assertEquals(List.of(1L), ids(customers.findByFilter(filter)));
        filter.setAddress(" ");
        assertEquals(List.of(1L, 2L), ids(customers.findByFilter(filter)));
    }

    @Test
    void filtersAreCombinedWithAnd() {
        CustomerFilterVM filter = new CustomerFilterVM();
        filter.setName("Muhammad");
        filter.setAddress("Pekalongan");
        assertTrue(customers.findByFilter(filter).isEmpty());
    }

    @Test
    void softDeletionHidesCustomerFromSearchAndExistenceChecks() {
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        assertEquals(1L, customers.deleteUpdateByIdIn(List.of(1L)));
        assertFalse(customers.findByIdAndDeletedAtIsNull(1L).isPresent());
        assertFalse(customers.existsByIdAndDeletedAtIsNull(1L));
        assertFalse(customers.existsByCodeIgnoreCaseAndDeletedAtIsNull("202104010001"));
        assertFalse(customers.existsByPhoneIgnoreCaseAndDeletedAtIsNull("088890909001"));
        assertFalse(customers.existsByEmailIgnoreCaseAndDeletedAtIsNull("MUHAMMAD@GMAIL.COM"));
        assertTrue(customers.findByKeyword("Muhammad").isEmpty());
        assertEquals(List.of(2L), ids(customers.findByFilter(new CustomerFilterVM())));
        assertEquals(List.of(2L), ids(customers.findByDeletedAtIsNull()));
        Customer deleted = customers.findById(1L).orElseThrow();
        assertNotNull(deleted.getDeletedAt());
        assertFalse(deleted.getDeletedAt().isBefore(before));
        assertNotNull(deleted.getUpdatedAt());
        assertTrue(customers.existsByIdAndDeletedAtIsNull(2L));
    }

    @Test
    void activeLookupsAndPrefixOrderingUseDatabaseContract() {
        assertTrue(customers.findByIdAndDeletedAtIsNull(1L).isPresent());
        assertTrue(customers.existsByCodeIgnoreCaseAndDeletedAtIsNull("202104010001"));
        assertTrue(customers.existsByPhoneIgnoreCaseAndDeletedAtIsNull("088890909001"));
        assertTrue(customers.existsByEmailIgnoreCaseAndDeletedAtIsNull("MUHAMMAD@GMAIL.COM"));
        assertEquals(
                "202104010002",
                customers.findFirstByCodeStartingWithOrderByCodeDesc("20210401").orElseThrow().getCode());
        assertFalse(customers.findFirstByCodeStartingWithOrderByCodeDesc("missing").isPresent());
    }
}
