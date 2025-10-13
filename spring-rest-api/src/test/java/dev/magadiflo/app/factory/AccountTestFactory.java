package dev.magadiflo.app.factory;

import dev.magadiflo.app.entity.Account;
import dev.magadiflo.app.entity.Bank;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.Arrays;

@UtilityClass
public class AccountTestFactory {
    public static Account createAccount(Long accountId, String holder, BigDecimal balance) {
        return Account.builder()
                .id(accountId)
                .holder(holder)
                .balance(balance)
                .build();
    }

    public static Bank createBank(Long bankId, String name, Account... accounts) {
        Bank bank = Bank.builder()
                .id(bankId)
                .name(name)
                .totalTransfers(0)
                .build();
        Arrays.stream(accounts).forEach(bank::addAccount);
        return bank;
    }
}
