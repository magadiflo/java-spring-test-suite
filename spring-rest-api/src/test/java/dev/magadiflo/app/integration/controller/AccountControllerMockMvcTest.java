package dev.magadiflo.app.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.magadiflo.app.constants.TestScripts;
import dev.magadiflo.app.dto.AccountCreateRequest;
import dev.magadiflo.app.dto.TransactionRequest;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@Tag("integration4")
@ActiveProfiles("test")
@Sql(scripts = {TestScripts.CLEANUP_MYSQL, TestScripts.DATA_TEST}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountControllerMockMvcTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldUseMySQLDatabase() throws SQLException {
        String url = this.dataSource.getConnection().getMetaData().getURL();
        log.info("Usando base de datos: {}", url);
        assertThat(url).contains("mysql", "db_spring_rest_api_test");
    }

    @Test
    void shouldReturnAllAccountsWhenTheyExist() throws Exception {
        // given
        List<String> expectedHolders = List.of(
                "Lesly Águila",
                "Cielo Fernández",
                "Susana Alvarado",
                "Briela Cirilo",
                "Milagros Díaz",
                "Kiara Lozano",
                "Analucía Urbina",
                "Yrma Guerrero"
        );

        // when
        ResultActions result = this.mockMvc.perform(get("/api/v1/accounts"));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(8)))
                .andExpect(jsonPath("$.size()", Matchers.is(8)))
                .andExpect(jsonPath("$[*].holder", Matchers.containsInAnyOrder(expectedHolders.toArray())));
    }

    @Test
    void shouldCreateNewAccountSuccessfully() throws Exception {
        // given
        AccountCreateRequest request = new AccountCreateRequest("Milagros", new BigDecimal("2000"), 2L);

        // when
        ResultActions result = this.mockMvc.perform(post("/api/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().string("Location", Matchers.containsString("/api/v1/accounts/9")))
                .andExpect(jsonPath("$.id", Matchers.is(9)))
                .andExpect(jsonPath("$.holder", Matchers.is("Milagros")))
                .andExpect(jsonPath("$.balance", Matchers.is(2000)))
                .andExpect(jsonPath("$.bankName", Matchers.is("BBVA")));
    }

    @Test
    void shouldReturnAccountDetailsWhenAccountExists() throws Exception {
        // given

        // when
        ResultActions result = this.mockMvc.perform(get("/api/v1/accounts/{accountId}", 4L));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.is(4)))
                .andExpect(jsonPath("$.holder", Matchers.is("Briela Cirilo")))
                .andExpect(jsonPath("$.balance").value(1000))
                .andExpect(jsonPath("$.bankName", Matchers.is("BBVA")));
    }

    @Test
    void shouldReturn404WhenAccountNotFound() throws Exception {
        // given
        Long accountId = 10L;

        // when
        ResultActions result = this.mockMvc.perform(get("/api/v1/accounts/{accountId}", accountId));

        // then
        result.andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("No se encontró la cuenta con ID: " + accountId))
                .andExpect(jsonPath("$.path").value("/api/v1/accounts/" + accountId));
    }

    @Test
    void shouldTransferMoneySuccessfully() throws Exception {
        // given
        var request = new TransactionRequest(6L, 8L, new BigDecimal("100"));

        // when
        ResultActions result = this.mockMvc.perform(post("/api/v1/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isNoContent())
                .andExpect(content().string(Matchers.is(Matchers.emptyString())));
    }

    @Test
    void shouldReturn400WhenBalanceIsInsufficient() throws Exception {
        // given
        var request = new TransactionRequest(6L, 8L, new BigDecimal("500"));

        // when
        ResultActions result = this.mockMvc.perform(post("/api/v1/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Saldo insuficiente en la cuenta del titular Kiara Lozano (ID: 6)"))
                .andExpect(jsonPath("$.path").value("/api/v1/accounts/transfer"));
    }
}
