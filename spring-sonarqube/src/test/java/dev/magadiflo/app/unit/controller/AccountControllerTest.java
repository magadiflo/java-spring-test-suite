package dev.magadiflo.app.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.magadiflo.app.controller.AccountController;
import dev.magadiflo.app.dto.AccountCreateRequest;
import dev.magadiflo.app.dto.AccountResponse;
import dev.magadiflo.app.dto.AccountUpdateRequest;
import dev.magadiflo.app.dto.DepositRequest;
import dev.magadiflo.app.dto.TransactionRequest;
import dev.magadiflo.app.dto.WithdrawalRequest;
import dev.magadiflo.app.exception.AccountNotFoundException;
import dev.magadiflo.app.exception.InsufficientBalanceException;
import dev.magadiflo.app.service.AccountService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("unit")
@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @MockitoBean
    private AccountService accountService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnAllAccountsWhenTheyExist() throws Exception {
        // given
        List<AccountResponse> accounts = List.of(
                new AccountResponse(1L, "Milagros", new BigDecimal("2000"), "BCP"),
                new AccountResponse(2L, "Kiara", new BigDecimal("1000"), "BCP")
        );
        Mockito.when(this.accountService.findAllAccounts()).thenReturn(accounts);

        // when
        ResultActions result = this.mockMvc.perform(get("/api/v1/accounts"));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(accounts.size())))
                .andExpect(jsonPath("$.size()", Matchers.is(accounts.size())))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].holder").value("Milagros"))
                .andExpect(jsonPath("$[0].balance").value(2000))
                .andExpect(jsonPath("$[0].bankName").value("BCP"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].holder").value("Kiara"))
                .andExpect(jsonPath("$[1].balance").value(1000))
                .andExpect(jsonPath("$[1].bankName").value("BCP"))
                // Verifica que la respuesta completa coincida con la serialización del objeto esperado
                .andExpect(content().json(this.objectMapper.writeValueAsString(accounts)));

        Mockito.verify(this.accountService).findAllAccounts();
        Mockito.verifyNoMoreInteractions(this.accountService);
    }

    @Test
    void shouldCreateNewAccountSuccessfully() throws Exception {
        // given
        AccountCreateRequest request = new AccountCreateRequest("Milagros", new BigDecimal("2000"), 1L);
        AccountResponse accountResponse = new AccountResponse(1L, "Milagros", new BigDecimal("2000"), "BCP");
        Mockito.when(this.accountService.saveAccount(request)).thenReturn(accountResponse);

        // when
        ResultActions result = this.mockMvc.perform(post("/api/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().string("Location", Matchers.containsString("/api/v1/accounts/1")))
                .andExpect(jsonPath("$.id", Matchers.is(1)))
                .andExpect(jsonPath("$.holder", Matchers.is("Milagros")))
                .andExpect(jsonPath("$.balance", Matchers.is(2000)))
                .andExpect(jsonPath("$.bankName", Matchers.is("BCP")));
        Mockito.verify(this.accountService).saveAccount(Mockito.any());
        Mockito.verifyNoMoreInteractions(this.accountService);
    }

    @Test
    void shouldReturnAccountDetailsWhenAccountExists() throws Exception {
        // given
        AccountResponse accountResponse = new AccountResponse(1L, "Milagros", new BigDecimal("2000"), "BCP");
        Mockito.when(this.accountService.findAccountById(1L)).thenReturn(accountResponse);

        // when
        ResultActions result = this.mockMvc.perform(get("/api/v1/accounts/{accountId}", 1L));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.is(1)))
                .andExpect(jsonPath("$.holder", Matchers.is("Milagros")))
                .andExpect(jsonPath("$.balance", Matchers.is(2000)))
                .andExpect(jsonPath("$.bankName", Matchers.is("BCP")));
        Mockito.verify(this.accountService).findAccountById(1L);
        Mockito.verifyNoMoreInteractions(this.accountService);
    }

    @Test
    void shouldReturn404WhenAccountNotFound() throws Exception {
        // given
        Long accountId = 1L;
        Mockito.when(this.accountService.findAccountById(accountId)).thenThrow(new AccountNotFoundException(accountId));

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
        Mockito.verify(this.accountService).findAccountById(accountId);
        Mockito.verifyNoMoreInteractions(this.accountService);
    }

    @Test
    void shouldTransferMoneySuccessfully() throws Exception {
        // given
        var request = new TransactionRequest(1L, 2L, new BigDecimal("500"));
        Mockito.doNothing().when(this.accountService).transfer(request);

        // when
        ResultActions result = this.mockMvc.perform(post("/api/v1/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isNoContent())
                .andExpect(content().string(Matchers.is(Matchers.emptyString())));
        Mockito.verify(this.accountService).transfer(Mockito.any());
        Mockito.verifyNoMoreInteractions(this.accountService);
    }

    @Test
    void shouldReturn400WhenBalanceIsInsufficient() throws Exception {
        // given
        var request = new TransactionRequest(1L, 2L, new BigDecimal("500"));
        Mockito.doThrow(new InsufficientBalanceException(1L, "Milagros")).when(this.accountService).transfer(request);

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
                .andExpect(jsonPath("$.message").value("Saldo insuficiente en la cuenta del titular Milagros (ID: 1)"))
                .andExpect(jsonPath("$.path").value("/api/v1/accounts/transfer"));
        Mockito.verify(this.accountService).transfer(Mockito.any());
        Mockito.verifyNoMoreInteractions(this.accountService);
    }

    @Test
    void shouldUpdateAccountSuccessfully() throws Exception {
        // given
        Long accountId = 1L;
        AccountUpdateRequest request = new AccountUpdateRequest("Milagros Díaz");
        AccountResponse updatedResponse = new AccountResponse(accountId, "Milagros Díaz", new BigDecimal("2500"), "BCP");
        Mockito.when(this.accountService.updateAccount(accountId, request)).thenReturn(updatedResponse);

        // when
        ResultActions result = this.mockMvc.perform(put("/api/v1/accounts/{accountId}", accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.is(1)))
                .andExpect(jsonPath("$.holder", Matchers.is("Milagros Díaz")))
                .andExpect(jsonPath("$.balance", Matchers.is(2500)))
                .andExpect(jsonPath("$.bankName", Matchers.is("BCP")));
        Mockito.verify(this.accountService).updateAccount(accountId, request);
        Mockito.verifyNoMoreInteractions(this.accountService);
    }

    @Test
    void shouldDeleteAccountSuccessfully() throws Exception {
        // given
        Long accountId = 1L;
        Mockito.doNothing().when(this.accountService).deleteAccount(accountId);

        // when
        ResultActions result = this.mockMvc.perform(delete("/api/v1/accounts/{accountId}", accountId));

        // then
        result.andExpect(status().isNoContent());
        Mockito.verify(this.accountService).deleteAccount(accountId);
        Mockito.verifyNoMoreInteractions(this.accountService);
    }

    @Test
    void shouldDepositSuccessfully() throws Exception {
        // given
        Long accountId = 1L;
        DepositRequest request = new DepositRequest(new BigDecimal("500"));
        AccountResponse response = new AccountResponse(accountId, "Milagros", new BigDecimal("2500"), "BCP");
        Mockito.when(this.accountService.deposit(accountId, request)).thenReturn(response);

        // when
        ResultActions result = this.mockMvc.perform(post("/api/v1/accounts/{accountId}/deposit", accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.is(1)))
                .andExpect(jsonPath("$.holder", Matchers.is("Milagros")))
                .andExpect(jsonPath("$.balance", Matchers.is(2500)))
                .andExpect(jsonPath("$.bankName", Matchers.is("BCP")));
        Mockito.verify(this.accountService).deposit(accountId, request);
        Mockito.verifyNoMoreInteractions(this.accountService);
    }

    @Test
    void shouldWithdrawSuccessfully() throws Exception {
        // given
        Long accountId = 1L;
        WithdrawalRequest request = new WithdrawalRequest(new BigDecimal("300"));
        AccountResponse response = new AccountResponse(accountId, "Milagros", new BigDecimal("1700"), "BCP");
        Mockito.when(this.accountService.withdraw(accountId, request)).thenReturn(response);

        // when
        ResultActions result = this.mockMvc.perform(post("/api/v1/accounts/{accountId}/withdraw", accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.is(1)))
                .andExpect(jsonPath("$.holder", Matchers.is("Milagros")))
                .andExpect(jsonPath("$.balance", Matchers.is(1700)))
                .andExpect(jsonPath("$.bankName", Matchers.is("BCP")));
        Mockito.verify(this.accountService).withdraw(accountId, request);
        Mockito.verifyNoMoreInteractions(this.accountService);
    }

}
