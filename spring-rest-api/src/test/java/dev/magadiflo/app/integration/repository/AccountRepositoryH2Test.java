package dev.magadiflo.app.integration.repository;

import dev.magadiflo.app.constants.TestScripts;
import dev.magadiflo.app.dto.AccountResponse;
import dev.magadiflo.app.entity.Account;
import dev.magadiflo.app.entity.Bank;
import dev.magadiflo.app.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ActiveProfiles("test-h2")
@Sql(scripts = TestScripts.DATA_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@DataJpaTest
class AccountRepositoryH2Test {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private DataSource dataSource;

    @Test
    void shouldUseH2Database() throws SQLException {
        String url = this.dataSource.getConnection().getMetaData().getURL();
        log.info("Usando base de datos: {}", url);
        assertThat(url).contains("h2");
    }

    @Test
    void shouldReturnAllAccountsWhenDatabaseIsInitialized() {
        // when
        List<AccountResponse> accounts = this.accountRepository.getAllAccounts();

        // then
        assertThat(accounts)
                .isNotEmpty()
                .hasSize(8)
                .extracting(AccountResponse::holder)
                .contains("Lesly Águila", "Briela Cirilo", "Milagros Díaz");
    }

    @Test
    void shouldHaveAssociatedBankForAllAccountsWhenFetchingAccounts() {
        // when
        List<AccountResponse> accounts = this.accountRepository.getAllAccounts();

        // then
        assertThat(accounts)
                .isNotEmpty()
                .hasSize(8)
                .allSatisfy(account -> {
                    assertThat(account.id()).isNotNull();
                    assertThat(account.holder()).isNotBlank();
                    assertThat(account.balance()).isNotNull().isPositive();
                    assertThat(account.bankName())
                            .withFailMessage("La cuenta con ID %d (titular: %s) debe tener banco asociado",
                                    account.id(), account.holder())
                            .isNotBlank();
                });

        // Verificación adicional: Al menos una cuenta es del BCP
        assertThat(accounts)
                .extracting(AccountResponse::bankName)
                .contains("BCP");
    }

    @Test
    void shouldReturnAccountWithBankWhenFindByIdExists() {
        // given
        Long accountId = 1L;

        // when
        Optional<Account> optionalAccount = this.accountRepository.findById(accountId);

        // then
        assertThat(optionalAccount)
                .isPresent()
                .hasValueSatisfying(account -> {
                    assertThat(account.getId()).isEqualTo(accountId);
                    assertThat(account.getHolder()).isEqualTo("Lesly Águila");
                    assertThat(account.getBalance()).isEqualByComparingTo("3000");
                    assertThat(account.getBank())
                            .isNotNull()
                            .extracting(Bank::getId, Bank::getName)
                            .containsExactly(1L, "BCP");
                });

    }

    @Test
    void shouldReturnEmptyOptionalWhenAccountIdDoesNotExist() {
        // given
        Long accountId = 999L;

        // when
        Optional<Account> optionalAccount = this.accountRepository.findById(accountId);

        // then
        assertThat(optionalAccount).isEmpty();

    }
}
