package dev.magadiflo.app.unit.service;

import dev.magadiflo.app.dto.AccountCreateRequest;
import dev.magadiflo.app.dto.AccountResponse;
import dev.magadiflo.app.dto.TransactionRequest;
import dev.magadiflo.app.dto.WithdrawalRequest;
import dev.magadiflo.app.entity.Account;
import dev.magadiflo.app.entity.Bank;
import dev.magadiflo.app.exception.AccountNotFoundException;
import dev.magadiflo.app.exception.InsufficientBalanceException;
import dev.magadiflo.app.factory.AccountTestFactory;
import dev.magadiflo.app.mapper.AccountMapper;
import dev.magadiflo.app.repository.AccountRepository;
import dev.magadiflo.app.repository.BankRepository;
import dev.magadiflo.app.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private BankRepository bankRepository;
    @Mock
    private AccountMapper accountMapper;
    @InjectMocks
    private AccountServiceImpl accountServiceUnderTest;

    @Test
    void shouldTransferBalanceWhenAccountsAreFromSameBank() {
        // given
        TransactionRequest request = new TransactionRequest(1L, 2L, new BigDecimal("700"));
        Account sourceAccount = AccountTestFactory.createAccount(1L, "Milagros", new BigDecimal("2000"));
        Account targetAccount = AccountTestFactory.createAccount(2L, "Kiara", new BigDecimal("1000"));
        Bank bank = AccountTestFactory.createBank(1L, "BCP", sourceAccount, targetAccount);

        Mockito.when(this.accountRepository.findById(1L)).thenReturn(Optional.of(sourceAccount));
        Mockito.when(this.accountRepository.findById(2L)).thenReturn(Optional.of(targetAccount));

        // when
        this.accountServiceUnderTest.transfer(request);

        // then
        assertThat(sourceAccount.getBalance()).isEqualByComparingTo("1300");
        assertThat(targetAccount.getBalance()).isEqualByComparingTo("1700");
        assertThat(bank.getTotalTransfers()).isEqualTo(1);
        Mockito.verify(this.accountRepository).findById(1L);
        Mockito.verify(this.accountRepository).findById(2L);
        Mockito.verify(this.accountRepository, Mockito.times(2)).findById(Mockito.anyLong());
        Mockito.verify(this.bankRepository, Mockito.never()).findById(Mockito.anyLong());
        Mockito.verify(this.accountRepository).save(sourceAccount);
        Mockito.verify(this.accountRepository).save(targetAccount);
        Mockito.verify(this.accountRepository, Mockito.times(2)).save(Mockito.any(Account.class));
        Mockito.verify(this.bankRepository).save(bank);
    }

    @Test
    void shouldThrowInsufficientBalanceExceptionWhenSourceAccountHasLowBalance() {
        // given
        TransactionRequest request = new TransactionRequest(1L, 2L, new BigDecimal("5000"));
        Account sourceAccount = AccountTestFactory.createAccount(1L, "Milagros", new BigDecimal("2000"));
        Account targetAccount = AccountTestFactory.createAccount(2L, "Kiara", new BigDecimal("1000"));
        Bank bank = AccountTestFactory.createBank(1L, "BCP", sourceAccount, targetAccount);

        Mockito.when(this.accountRepository.findById(1L)).thenReturn(Optional.of(sourceAccount));
        Mockito.when(this.accountRepository.findById(2L)).thenReturn(Optional.of(targetAccount));

        // when
        assertThatThrownBy(() -> this.accountServiceUnderTest.transfer(request))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessage("Saldo insuficiente en la cuenta del titular Milagros (ID: 1)");

        // then
        assertThat(sourceAccount.getBalance()).isEqualByComparingTo("2000");
        assertThat(targetAccount.getBalance()).isEqualByComparingTo("1000");
        assertThat(bank.getTotalTransfers()).isZero();
        Mockito.verify(this.accountRepository).findById(1L);
        Mockito.verify(this.accountRepository).findById(2L);
        Mockito.verify(this.accountRepository, Mockito.times(2)).findById(Mockito.anyLong());
        Mockito.verify(this.bankRepository, Mockito.never()).findById(Mockito.anyLong());
        Mockito.verify(this.accountRepository, Mockito.never()).save(sourceAccount);
        Mockito.verify(this.accountRepository, Mockito.never()).save(targetAccount);
        Mockito.verify(this.bankRepository, Mockito.never()).save(bank);
    }

    @Test
    void shouldReturnAccountResponseWhenAccountExists() {
        // given
        Account account = AccountTestFactory.createAccount(1L, "Milagros", new BigDecimal("2000"));
        Bank bank = AccountTestFactory.createBank(1L, "BCP", account);
        AccountResponse accountResponse = new AccountResponse(account.getId(), account.getHolder(), account.getBalance(), account.getBank().getName());

        Mockito.when(this.accountRepository.findById(1L)).thenReturn(Optional.of(account));
        Mockito.when(this.accountMapper.toAccountResponse(account)).thenReturn(accountResponse);

        // when
        AccountResponse result = this.accountServiceUnderTest.findAccountById(1L);

        // then
        assertThat(result)
                .isNotNull()
                .isSameAs(accountResponse);
        assertThat(result)
                .extracting(AccountResponse::id, AccountResponse::holder, AccountResponse::balance, AccountResponse::bankName)
                .containsExactly(1L, "Milagros", new BigDecimal("2000"), bank.getName());
        Mockito.verify(this.accountRepository).findById(1L);
        Mockito.verify(this.accountMapper).toAccountResponse(account);
    }

    @Test
    void shouldThrowAccountNotFoundExceptionWhenAccountDoesNotExist() {
        // given
        Mockito.when(this.accountRepository.findById(1L)).thenReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> this.accountServiceUnderTest.findAccountById(1L))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessage("No se encontró la cuenta con ID: 1");

        // then
        Mockito.verify(this.accountRepository).findById(1L);
        Mockito.verify(this.accountMapper, Mockito.never()).toAccountResponse(Mockito.any(Account.class));
    }

    @Test
    void shouldGetBalanceOfAnAccountWhenAccountExists() {
        // given
        Account account = AccountTestFactory.createAccount(1L, "Milagros", new BigDecimal("2000"));

        Mockito.when(this.accountRepository.findById(1L)).thenReturn(Optional.of(account));

        // when
        BigDecimal result = this.accountServiceUnderTest.getAccountBalance(1L);

        // then
        assertThat(result).isEqualByComparingTo(account.getBalance());
        Mockito.verify(this.accountRepository).findById(1L);
    }

    @Test
    void shouldGetTotalTransfersWhenBankExists() {
        // given
        Bank bank = AccountTestFactory.createBank(1L, "BCP");
        bank.setTotalTransfers(10);

        Mockito.when(this.bankRepository.findById(1L)).thenReturn(Optional.of(bank));

        // when
        int result = this.accountServiceUnderTest.countTotalTransfersToBank(1L);

        // then
        assertThat(result).isEqualTo(10);
        Mockito.verify(this.bankRepository).findById(1L);
    }

    @Test
    void shouldGetAllAccountsWhenAccountsExists() {
        // given
        List<AccountResponse> accountResponses = List.of(
                new AccountResponse(1L, "Milagros", new BigDecimal("2000"), "BCP"),
                new AccountResponse(2L, "Kiara", new BigDecimal("1000"), "BCP")
        );
        Mockito.when(this.accountRepository.getAllAccounts()).thenReturn(accountResponses);

        // when
        List<AccountResponse> result = this.accountServiceUnderTest.findAllAccounts();

        // then
        assertThat(result)
                .isNotEmpty()
                .hasSize(2)
                .containsExactlyElementsOf(accountResponses);
        Mockito.verify(this.accountRepository).getAllAccounts();
    }

    @Test
    void shouldSaveNewAccountWhenBankExists() {
        // given
        AccountCreateRequest accountRequest = AccountTestFactory.createAccountRequest("Milagros", new BigDecimal("2000"), 1L);
        Bank bank = AccountTestFactory.createBank(1L, "BCP");
        Account accountWithoutId = AccountTestFactory.createAccountWithoutId(accountRequest, bank);
        Account accountWithId = AccountTestFactory.createAccountWithId(10L, accountRequest, bank);
        AccountResponse expectedResponse = AccountTestFactory.toAccountResponse(accountWithId);

        assertThat(accountWithoutId.getId()).isNull(); // Verificamos que no tenga ID antes del save

        Mockito.when(this.bankRepository.findById(1L)).thenReturn(Optional.of(bank));
        Mockito.when(this.accountMapper.toAccount(accountRequest, bank)).thenReturn(accountWithoutId);

        Mockito.doAnswer(invocation -> {
            Account saved = invocation.getArgument(0);
            saved.setId(10L); // Simula el comportamiento de JPA: asigna el ID directamente al objeto original
            return saved;
        }).when(this.accountRepository).save(accountWithoutId);

        Mockito.when(this.accountMapper.toAccountResponse(accountWithoutId)) // El objeto accountWithoutId ya tiene ID asignado
                .thenReturn(expectedResponse);

        // when
        AccountResponse actualResponse = this.accountServiceUnderTest.saveAccount(accountRequest);

        // then
        assertThat(accountWithoutId.getId())
                .isNotNull() // Verificación explícita del cambio de estado
                .isEqualTo(10L);
        assertThat(actualResponse)
                .isNotNull()
                .extracting(AccountResponse::id, AccountResponse::holder, AccountResponse::balance, AccountResponse::bankName)
                .containsExactly(10L, "Milagros", new BigDecimal("2000"), "BCP");
        Mockito.verify(this.bankRepository).findById(1L);
        Mockito.verify(this.accountMapper).toAccount(accountRequest, bank);
        Mockito.verify(this.accountRepository).save(accountWithoutId);
        Mockito.verify(this.accountMapper).toAccountResponse(accountWithoutId);
    }

    @Test
    void shouldWithdrawAmountSuccessfullyWhenAccountExists() {
        // given
        Account accountBeforeWithdrawal = AccountTestFactory.createAccount(1L, "Milagros", new BigDecimal("2000"));
        Account accountAfterWithdrawal = AccountTestFactory.createAccount(1L, "Milagros", new BigDecimal("1500"));
        Bank bank = AccountTestFactory.createBank(1L, "BCP", accountBeforeWithdrawal, accountAfterWithdrawal);
        WithdrawalRequest request = new WithdrawalRequest(new BigDecimal("500"));
        AccountResponse expectedResponse = AccountTestFactory.toAccountResponse(accountAfterWithdrawal);

        Mockito.when(this.accountRepository.findById(1L)).thenReturn(Optional.of(accountBeforeWithdrawal));
        Mockito.when(this.accountRepository.save(accountBeforeWithdrawal)).thenReturn(accountBeforeWithdrawal); // Ya mutado
        Mockito.when(this.accountMapper.toAccountResponse(accountBeforeWithdrawal)).thenReturn(expectedResponse);

        // when
        AccountResponse actualResponse = this.accountServiceUnderTest.withdraw(1L, request);

        // then
        assertThat(accountBeforeWithdrawal.getBalance())
                .isEqualByComparingTo("1500");
        assertThat(actualResponse)
                .isNotNull()
                .extracting(AccountResponse::id, AccountResponse::holder, AccountResponse::balance, AccountResponse::bankName)
                .containsExactly(1L, "Milagros", new BigDecimal("1500"), bank.getName());
        Mockito.verify(this.accountRepository).findById(1L);
        Mockito.verify(this.accountRepository).save(accountBeforeWithdrawal);
        Mockito.verify(this.accountMapper).toAccountResponse(accountBeforeWithdrawal);
    }

    @Test
    void shouldThrowInsufficientBalanceExceptionWhenAccountHasLowBalance() {
        // given
        Account account = AccountTestFactory.createAccount(1L, "Milagros", new BigDecimal("1000"));
        WithdrawalRequest request = new WithdrawalRequest(new BigDecimal("1200"));

        Mockito.when(this.accountRepository.findById(1L)).thenReturn(Optional.of(account));

        // when
        assertThatThrownBy(() -> this.accountServiceUnderTest.withdraw(1L, request))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessage("Saldo insuficiente en la cuenta del titular Milagros (ID: 1)");

        // then
        assertThat(account.getBalance()).isEqualByComparingTo("1000");
        Mockito.verify(this.accountRepository).findById(1L);
        Mockito.verify(this.accountRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(this.accountMapper, Mockito.never()).toAccountResponse(Mockito.any());
    }

    @Test
    void shouldThrowAccountNotFoundExceptionWhenAccountDoesNotExistDuringWithdrawal() {
        // given
        WithdrawalRequest request = new WithdrawalRequest(new BigDecimal("1200"));
        Mockito.when(this.accountRepository.findById(1L)).thenReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> this.accountServiceUnderTest.withdraw(1L, request))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessage("No se encontró la cuenta con ID: 1");

        // then
        Mockito.verify(this.accountRepository).findById(1L);
        Mockito.verifyNoMoreInteractions(this.accountRepository, this.accountMapper);
    }
}
