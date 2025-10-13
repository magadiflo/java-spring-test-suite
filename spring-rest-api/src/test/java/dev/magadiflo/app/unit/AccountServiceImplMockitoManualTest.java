package dev.magadiflo.app.unit;

import dev.magadiflo.app.dto.AccountResponse;
import dev.magadiflo.app.dto.TransactionRequest;
import dev.magadiflo.app.entity.Account;
import dev.magadiflo.app.entity.Bank;
import dev.magadiflo.app.exception.AccountNotFoundException;
import dev.magadiflo.app.exception.InsufficientBalanceException;
import dev.magadiflo.app.factory.AccountTestFactory;
import dev.magadiflo.app.mapper.AccountMapper;
import dev.magadiflo.app.repository.AccountRepository;
import dev.magadiflo.app.repository.BankRepository;
import dev.magadiflo.app.service.AccountService;
import dev.magadiflo.app.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountServiceImplMockitoManualTest {

    private AccountRepository accountRepository;
    private BankRepository bankRepository;
    private AccountMapper accountMapper;
    private AccountService accountServiceUnderTest;

    @BeforeEach
    void setUp() {
        this.accountRepository = Mockito.mock(AccountRepository.class);
        this.bankRepository = Mockito.mock(BankRepository.class);
        this.accountMapper = Mockito.mock(AccountMapper.class);
        this.accountServiceUnderTest = new AccountServiceImpl(this.accountRepository, this.bankRepository, this.accountMapper);
    }

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
}
