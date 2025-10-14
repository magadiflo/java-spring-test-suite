package dev.magadiflo.app.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.magadiflo.app.controller.AccountController;
import dev.magadiflo.app.dto.AccountResponse;
import dev.magadiflo.app.service.AccountService;
import org.hamcrest.Matchers;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
}
