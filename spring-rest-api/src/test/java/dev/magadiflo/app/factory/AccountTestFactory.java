package dev.magadiflo.app.factory;

import dev.magadiflo.app.dto.AccountCreateRequest;
import dev.magadiflo.app.dto.AccountResponse;
import dev.magadiflo.app.entity.Account;
import dev.magadiflo.app.entity.Bank;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.Arrays;

@UtilityClass
public class AccountTestFactory {

    public static Bank createBank(Long bankId, String name, Account... accounts) {
        Bank bank = Bank.builder()
                .id(bankId)
                .name(name)
                .totalTransfers(0)
                .build();
        Arrays.stream(accounts).forEach(bank::addAccount);
        return bank;
    }

    public static Account createAccount(Long accountId, String holder, BigDecimal balance) {
        return Account.builder()
                .id(accountId)
                .holder(holder)
                .balance(balance)
                .build();
    }

    public static Account createAccountWithoutId(AccountCreateRequest request, Bank bank) {
        return Account.builder()
                .holder(request.holder())
                .balance(request.balance())
                .bank(bank)
                .build();
    }

    public static Account createAccountWithId(Long expectedAccountId, AccountCreateRequest request, Bank bank) {
        return Account.builder()
                .id(expectedAccountId)
                .holder(request.holder())
                .balance(request.balance())
                .bank(bank)
                .build();
    }

    public static AccountResponse toAccountResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getHolder(),
                account.getBalance(),
                account.getBank().getName()
        );
    }

    public static AccountCreateRequest createAccountRequest(String holder, BigDecimal balance, Long bankId) {
        return new AccountCreateRequest(holder, balance, bankId);
    }
}
