package dev.magadiflo.app.unit;

import dev.magadiflo.app.dto.TransactionRequest;
import dev.magadiflo.app.entity.Account;
import dev.magadiflo.app.entity.Bank;
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
}
