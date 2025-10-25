package dev.magadiflo.testcontainers.app.integration.repository;

import dev.magadiflo.testcontainers.app.commons.AbstractPostgresAutomaticTest;
import dev.magadiflo.testcontainers.app.constants.TestScripts;
import dev.magadiflo.testcontainers.app.entity.Customer;
import dev.magadiflo.testcontainers.app.repository.CustomerRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("testcontainers")
@ActiveProfiles("test")
@Sql(scripts = TestScripts.DATA_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryAutomaticTestcontainersTest extends AbstractPostgresAutomaticTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void shouldReturnAllCustomersWhenDatabaseIsInitialized() {
        // when
        List<Customer> customers = this.customerRepository.findAll();

        // then
        assertThat(customers)
                .isNotEmpty()
                .hasSize(8)
                .extracting(Customer::getName)
                .contains("Lesly Águila", "Briela Cirilo", "Milagros Díaz");
    }

    @Test
    void shouldFindCustomerWhenValidEmail() {
        assertThat(this.customerRepository.findByEmail("yrmagerreron@outlook.com"))
                .isPresent()
                .hasValueSatisfying(customer -> {
                    assertThat(customer.getId()).isEqualTo(3);
                    assertThat(customer.getName()).isEqualTo("Yrma Guerrero");
                });
    }

    @Test
    void shouldSaveCustomer() {
        // given
        Customer customer = Customer.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        // when
        Customer savedCustomer = this.customerRepository.save(customer);

        // then
        assertThat(savedCustomer).isNotNull();
        assertThat(savedCustomer.getId()).isNotNull();
        assertThat(savedCustomer.getName()).isEqualTo("John Doe");
        assertThat(savedCustomer.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void shouldDeleteAllCustomers() {
        // given
        assertThat(this.customerRepository.count()).isEqualTo(8);

        // when
        this.customerRepository.deleteAll();

        // then
        assertThat(this.customerRepository.count()).isZero();
    }
}
