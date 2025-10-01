package dev.magadiflo.junit5.app.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Bank {
    private String name;
    private List<Account> accounts = new ArrayList<>(); //importante inicializarlo sino lanzaría un NullPointerExc.

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public void addAccount(Account account) {
        this.accounts.add(account); //Si accounts no está inicializada al usar el .add() lanzaría el NullPointer..
        account.setBank(this);
    }

    public void transfer(Account source, Account target, BigDecimal amount) {
        source.debit(amount);
        target.credit(amount);
    }
}
