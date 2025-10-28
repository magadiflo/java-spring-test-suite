package dev.magadiflo.app.integration.controller;

import dev.magadiflo.app.constants.TestScripts;
import dev.magadiflo.app.dto.AccountCreateRequest;
import dev.magadiflo.app.dto.AccountResponse;
import dev.magadiflo.app.dto.TransactionRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * Esta clase forma parte del conjunto de pruebas de integración del proyecto.
 * Nota: Estas pruebas no se utilizan para el cálculo de cobertura de código,
 * ya que JaCoCo únicamente considera pruebas unitarias.
 * Se mantienen para simular un entorno empresarial real con múltiples niveles de testing.
 */

@Slf4j
@Tag("integration")
@ActiveProfiles("test")
@Sql(scripts = {TestScripts.CLEANUP_MYSQL, TestScripts.DATA_TEST}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountControllerIT {

    @Autowired
    private WebTestClient client;

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
        WebTestClient.ResponseSpec response = this.client
                .post()
                .uri("/api/v1/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange();

        // then
        response.expectStatus().isNoContent()
                .expectBody().isEmpty();
    }

    @Test
    void shouldReturnAllAccountsWhenTheyExist() {
        // given

        // when
        WebTestClient.ResponseSpec response = this.client
                .get()
                .uri("/api/v1/accounts")
                .exchange();

        // then
        response.expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(AccountResponse.class)
                .consumeWith(result -> {
                    List<AccountResponse> accountResponseList = result.getResponseBody();
                    assertThat(accountResponseList)
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
                    assertThat(accountResponseList.stream().filter(a -> a.id().equals(1L)).findFirst())
                            .isPresent()
                            .hasValueSatisfying(accountResponse -> {
                                assertThat(accountResponse.holder()).isEqualTo("Lesly Águila");
                                assertThat(accountResponse.balance()).isEqualByComparingTo("3000");
                                assertThat(accountResponse.bankName()).isEqualTo("BCP");
                            });
                });
    }

    @Test
    void shouldCreateNewAccountSuccessfully() {
        // given
        AccountCreateRequest request = new AccountCreateRequest("Milagros", new BigDecimal("2000"), 1L);

        // when
        WebTestClient.ResponseSpec response = this.client
                .post()
                .uri("/api/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange();

        // then
        response.expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectHeader().valueMatches("Location", "http://localhost:\\d+/api/v1/accounts/\\d+")
                .expectBody(AccountResponse.class)
                .consumeWith(result -> {
                    AccountResponse accountResponse = result.getResponseBody();
                    assertThat(accountResponse)
                            .isNotNull()
                            .extracting(AccountResponse::id, AccountResponse::holder, AccountResponse::balance, AccountResponse::bankName)
                            .containsExactly(9L, request.holder(), request.balance(), "BCP");
                });
    }

    @Test
    void shouldReturnAccountDetailsWhenAccountExists() {
        // given
        long accountId = 5L;

        // when
        WebTestClient.ResponseSpec response = this.client
                .get()
                .uri("/api/v1/accounts/{accountId}", accountId)
                .exchange();

        // then
        response.expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(5L)
                .jsonPath("$.holder").isEqualTo("Milagros Díaz")
                .jsonPath("$.balance").isEqualTo(3500)
                .jsonPath("$.bankName").isEqualTo("Interbank");
    }

    @Test
    void shouldReturn404WhenAccountNotFound() {
        // given
        long accountId = 10L;

        // when
        WebTestClient.ResponseSpec response = this.client
                .get()
                .uri("/api/v1/accounts/{accountId}", accountId)
                .exchange();

        // then
        response.expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.timestamp").exists()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Not Found")
                .jsonPath("$.message").isEqualTo("No se encontró la cuenta con ID: " + accountId)
                .jsonPath("$.path").isEqualTo("/api/v1/accounts/" + accountId);
    }

    @Test
    void shouldReturn400WhenBalanceIsInsufficient() {
        // given
        var request = new TransactionRequest(1L, 2L, new BigDecimal("5000"));

        // when
        WebTestClient.ResponseSpec response = this.client
                .post()
                .uri("/api/v1/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange();

        // then
        response.expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.timestamp").exists()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Bad Request")
                .jsonPath("$.message").isEqualTo("Saldo insuficiente en la cuenta del titular Lesly Águila (ID: 1)")
                .jsonPath("$.path").isEqualTo("/api/v1/accounts/transfer");
    }
}
