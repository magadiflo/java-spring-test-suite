package dev.magadiflo.app.integration.controller;

import dev.magadiflo.app.constants.TestScripts;
import dev.magadiflo.app.dto.AccountCreateRequest;
import dev.magadiflo.app.dto.AccountResponse;
import dev.magadiflo.app.dto.ErrorResponse;
import dev.magadiflo.app.dto.TransactionRequest;
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

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatList;

@Slf4j
@Tag("integration3")
@ActiveProfiles("test")
@Sql(scripts = {TestScripts.CLEANUP_MYSQL, TestScripts.DATA_TEST}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountControllerTestRestTemplateTest {

    @Autowired
    private TestRestTemplate client;

    @Autowired
    private DataSource dataSource;

    @Test
    void shouldUseMySQLDatabase() throws SQLException {
        String url = this.dataSource.getConnection().getMetaData().getURL();
        log.info("Usando base de datos: {}", url);
        assertThat(url).contains("mysql", "db_spring_rest_api_test");
    }

    @Test
    void shouldTransferMoneySuccessfully() {
        // given
        var request = new TransactionRequest(1L, 2L, new BigDecimal("2999"));

        // when
        ResponseEntity<Void> response = this.client.postForEntity("/api/v1/accounts/transfer", request, Void.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void shouldReturnAllAccountsWhenTheyExist() {
        // given

        // when
        ResponseEntity<AccountResponse[]> response = this.client.getForEntity("/api/v1/accounts", AccountResponse[].class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(8);
        assertThatList(List.of(response.getBody()))
                .hasSize(8)
                .extracting(AccountResponse::holder)
                .containsExactlyInAnyOrder(
                        "Lesly Águila",
                        "Cielo Fernández",
                        "Susana Alvarado",
                        "Briela Cirilo",
                        "Milagros Díaz",
                        "Kiara Lozano",
                        "Analucía Urbina",
                        "Yrma Guerrero");
        assertThat(Stream.of(response.getBody()).filter(a -> a.id().equals(1L)).findFirst())
                .isPresent()
                .hasValueSatisfying(accountResponse -> {
                    assertThat(accountResponse.holder()).isEqualTo("Lesly Águila");
                    assertThat(accountResponse.balance()).isEqualByComparingTo("3000");
                    assertThat(accountResponse.bankName()).isEqualTo("BCP");
                });
    }

    @Test
    void shouldCreateNewAccountSuccessfully() {
        // given
        AccountCreateRequest request = new AccountCreateRequest("Milagros", new BigDecimal("2000"), 1L);

        // when
        ResponseEntity<AccountResponse> response = this.client.postForEntity("/api/v1/accounts", request, AccountResponse.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getHeaders().getLocation())
                .isNotNull()
                .asString()
                .matches("http://localhost:\\d+/api/v1/accounts/\\d+");

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody())
                .isNotNull()
                .extracting(AccountResponse::id, AccountResponse::holder, AccountResponse::balance, AccountResponse::bankName)
                .containsExactly(9L, request.holder(), request.balance(), "BCP");
    }

    @Test
    void shouldReturnAccountDetailsWhenAccountExists() {
        // given
        long accountId = 5L;

        // when
        ResponseEntity<AccountResponse> response = this.client.getForEntity("/api/v1/accounts/{accountId}", AccountResponse.class, accountId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getBody())
                .isNotNull()
                .satisfies(accountResponse -> {
                    assertThat(accountResponse.id()).isEqualTo(5);
                    assertThat(accountResponse.holder()).isEqualTo("Milagros Díaz");
                    assertThat(accountResponse.balance()).isEqualByComparingTo("3500");
                    assertThat(accountResponse.bankName()).isEqualTo("Interbank");
                });
    }

    @Test
    void shouldReturn404WhenAccountNotFound() {
        // given
        long accountId = 10L;

        // when
        ResponseEntity<ErrorResponse> response = this.client.getForEntity("/api/v1/accounts/{accountId}", ErrorResponse.class, accountId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getBody())
                .isNotNull()
                .satisfies(errorResponse -> {
                    assertThat(errorResponse.timestamp()).isNotNull();
                    assertThat(errorResponse.status()).isEqualTo(404);
                    assertThat(errorResponse.error()).isEqualTo("Not Found");
                    assertThat(errorResponse.message()).isEqualTo("No se encontró la cuenta con ID: " + accountId);
                    assertThat(errorResponse.path()).isEqualTo("/api/v1/accounts/" + accountId);
                });
    }

    @Test
    void shouldReturn400WhenBalanceIsInsufficient() {
        // given
        var request = new TransactionRequest(1L, 2L, new BigDecimal("5000"));

        // when
        ResponseEntity<ErrorResponse> response = this.client.postForEntity("/api/v1/accounts/transfer", request, ErrorResponse.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getBody())
                .isNotNull()
                .satisfies(errorResponse -> {
                    assertThat(errorResponse.timestamp()).isNotNull();
                    assertThat(errorResponse.status()).isEqualTo(400);
                    assertThat(errorResponse.error()).isEqualTo("Bad Request");
                    assertThat(errorResponse.message()).isEqualTo("Saldo insuficiente en la cuenta del titular Lesly Águila (ID: 1)");
                    assertThat(errorResponse.path()).isEqualTo("/api/v1/accounts/transfer");
                });
    }
}
