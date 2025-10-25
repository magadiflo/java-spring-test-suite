package dev.magadiflo.testcontainers.app.integration.controller;

import dev.magadiflo.testcontainers.app.commons.AbstractPostgresManualTest;
import dev.magadiflo.testcontainers.app.constants.TestScripts;
import dev.magadiflo.testcontainers.app.entity.Customer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatList;

@Slf4j
@Tag("testcontainers")
@ActiveProfiles("test")
@Sql(scripts = {TestScripts.CLEANUP_POSTGRES, TestScripts.DATA_TEST}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerControllerManualTestcontainersTest extends AbstractPostgresManualTest {

    @Autowired
    private TestRestTemplate client;

    @Test
    void shouldReturnAllCustomersWhenTheyExist() {
        // given

        // when
        ResponseEntity<Customer[]> response = this.client.getForEntity("/api/v1/customers", Customer[].class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(8);
        assertThatList(List.of(response.getBody()))
                .hasSize(8)
                .extracting(Customer::getName)
                .containsExactlyInAnyOrder(
                        "Lesly Águila",
                        "Cielo Fernández",
                        "Susana Alvarado",
                        "Briela Cirilo",
                        "Milagros Díaz",
                        "Kiara Lozano",
                        "Analucía Urbina",
                        "Yrma Guerrero");
        assertThat(response.getBody())
                .filteredOn(customer -> customer.getId().equals(1L))
                .singleElement()
                .satisfies(customer -> {
                    assertThat(customer.getName()).isEqualTo("Milagros Díaz");
                    assertThat(customer.getEmail()).isEqualTo("milagros@gmail.com");
                });
    }

    @Test
    void shouldCreateNewCustomerSuccessfully() {
        // given
        Customer request = Customer.builder()
                .name("Nicol Sinchi")
                .email("nicol@gmail.com")
                .build();

        // when
        ResponseEntity<Customer> response = this.client.postForEntity("/api/v1/customers", request, Customer.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getBody())
                .isNotNull()
                .extracting(Customer::getId, Customer::getName, Customer::getEmail)
                .containsExactly(9L, request.getName(), request.getEmail());
    }

    @Test
    void shouldReturnCustomerDetailsWhenCustomerExists() {
        // given
        long customerId = 5L;

        // when
        ResponseEntity<Customer> response = this.client.getForEntity("/api/v1/customers/{customerId}", Customer.class, customerId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getBody())
                .isNotNull()
                .satisfies(customer -> {
                    assertThat(customer.getId()).isEqualTo(5);
                    assertThat(customer.getName()).isEqualTo("Briela Cirilo");
                    assertThat(customer.getEmail()).isEqualTo("briela@gmail.com");
                });
    }
}
