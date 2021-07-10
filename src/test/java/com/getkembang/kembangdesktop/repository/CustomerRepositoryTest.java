package com.getkembang.kembangdesktop.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import com.getkembang.kembangdesktop.domain.Customer;
import com.getkembang.kembangdesktop.viewmodel.CustomerAddVM;
import com.getkembang.kembangdesktop.viewmodel.CustomerEditVM;
import com.getkembang.kembangdesktop.viewmodel.CustomerFilterVM;
import com.github.database.rider.core.api.dataset.DataSet;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DataSet("customer.yml")
class CustomerRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void testFilter_shouldReturnFilteredCustomer() {
        CustomerFilterVM filter = new CustomerFilterVM();
        filter.setName("Muhammad");
        filter.setCode("202104010001");
        filter.setEmail("muhammad@gmail.com");
        filter.setPhone("088890909001");
        filter.setAddress("Jakarta");
        List<Customer> customers = customerRepository.filter(filter);
        assertThat(customers, hasSize(1));
        assertThat(customers.get(0), allOf(
            hasProperty("id", is(1l)),
            hasProperty("code", is("202104010001")),
            hasProperty("name", is("Muhammad")),
            hasProperty("phone", is("088890909001")),
            hasProperty("email", is("muhammad@gmail.com")),
            hasProperty("address", is("Jakarta"))));
        filter = new CustomerFilterVM();
        customers = customerRepository.filter(filter);
        assertThat(customers, hasSize(2));
        assertThat(customers,
                hasItems(
                    hasProperty("id", is(1l)),
                    hasProperty("id", is(2l)),
                    hasProperty("code", is("202104010001")),
                    hasProperty("code", is("202104010002")),
                    hasProperty("name", is("Muhammad")),
                    hasProperty("name", is("Ismail")),
                    hasProperty("phone", is("088890909001")),
                    hasProperty("phone", is("088890909002")),
                    hasProperty("email", is("muhammad@gmail.com")),
                    hasProperty("email", is("ismail@gmail.com")),
                    hasProperty("address", is("Jakarta")),
                    hasProperty("address", is("Pekalongan"))));
    }

    @Test
    void testCreateCustomer_shouldSucceed() {
        CustomerAddVM customerAdd = new CustomerAddVM();
        customerAdd.setName("Diana");
        customerAdd.setCode("202104010003");
        customerAdd.setEmail("diana@gmail.com");
        customerAdd.setPhone("088890909003");
        customerAdd.setAddress("Jakarta");
        customerRepository.createCustomer(customerAdd);
        assertEquals(3, customerRepository.count());
    }

    @Test
    void testUpdateCustomer_shouldSucceed() {
        CustomerEditVM customerEdit = new CustomerEditVM();
        customerEdit.setName("Muhammad");
        customerEdit.setCode("202104010001");
        customerEdit.setEmail("muhammad@yahoo.com");
        customerEdit.setPhone("088890909001");
        customerEdit.setAddress("Jakarta");
        customerEdit.setId(1L);
        Integer rowsAffected = customerRepository.updateCustomer(customerEdit);
        assertEquals(1, rowsAffected.intValue());

        Optional<Customer> customer = customerRepository.readOne(1L);
        assertTrue(customer.isPresent());
        assertEquals(customerEdit.getEmail(), customer.get().getEmail());
    }

    @Test
    void testExistsByCode_shouldReturnFalse() {
        String code = "202104010001";
        boolean exists = customerRepository.existsByCode(code, 1L);
        assertFalse(exists);
    }

    @Test
    void testExistsByEmail_shouldReturnFalse() {
        String email = "ismail@gmail.com";
        boolean exists = customerRepository.existsByEmail(email, 2L);
        assertFalse(exists);
    }

    @Test
    void testExistsByPhone_shouldReturnFalse() {
        String phone = "202104010002";
        boolean exists = customerRepository.existsByPhone(phone, 2L);
        assertFalse(exists);
    }

    @Test
    void testfindMaxCodeByPrefix_shouldSucceed() {
        String prefix = "20210401";
        String code = customerRepository.findMaxCodeByPrefix(prefix);
        assertEquals("202104010002", code);
    }

}
