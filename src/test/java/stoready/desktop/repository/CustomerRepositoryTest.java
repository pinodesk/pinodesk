package stoready.desktop.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.List;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import stoready.desktop.domain.Customer;
import stoready.desktop.viewmodel.CustomerFilterVM;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DatabaseSetup("CustomerRepositoryTest.xml")
class CustomerRepositoryTest extends RepositoryTestBase {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void testFindByFilter_shouldReturnFilteredCustomer() {
        CustomerFilterVM filter = new CustomerFilterVM();
        filter.setName("Muhammad");
        filter.setCode("202104010001");
        filter.setEmail("muhammad@gmail.com");
        filter.setPhone("088890909001");
        filter.setAddress("Jakarta");
        List<Customer> customers = customerRepository.findByFilter(filter);
        assertThat(customers, hasSize(1));
        assertThat(
                customers.get(0),
                allOf(
                        hasProperty("id", is(1l)),
                        hasProperty("code", is("202104010001")),
                        hasProperty("name", is("Muhammad")),
                        hasProperty("phone", is("088890909001")),
                        hasProperty("email", is("muhammad@gmail.com")),
                        hasProperty("address", is("Jakarta"))));
        filter = new CustomerFilterVM();
        customers = customerRepository.findByFilter(filter);
        assertThat(customers, hasSize(2));
        assertThat(
                customers,
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

}
