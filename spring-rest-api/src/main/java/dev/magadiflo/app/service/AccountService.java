package dev.magadiflo.app.service;

import dev.magadiflo.app.dto.AccountCreateRequest;
import dev.magadiflo.app.dto.AccountResponse;
import dev.magadiflo.app.dto.AccountUpdateRequest;
import dev.magadiflo.app.dto.DepositRequest;
import dev.magadiflo.app.dto.TransactionRequest;
import dev.magadiflo.app.dto.WithdrawalRequest;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {
    // ========= CONSULTAS =========
    List<AccountResponse> findAllAccounts();

    AccountResponse findAccountById(Long accountId);

    AccountResponse findAccountByHolder(String holder);

    BigDecimal getAccountBalance(Long accountId);

    // ========= OPERACIONES CRUD =========
    void createAccount(AccountCreateRequest accountRequest);

    AccountResponse saveAccount(AccountCreateRequest accountRequest);

    AccountResponse updateAccount(Long accountId, AccountUpdateRequest accountRequest);

    void deleteAccount(Long accountId);

    // ========= OPERACIONES TRANSACCIONALES =========
    AccountResponse deposit(Long accountId, DepositRequest request);

    AccountResponse withdraw(Long accountId, WithdrawalRequest request);

    void transfer(TransactionRequest request);

    // ========= REPORTES / CONSULTAS AGREGADAS =========
    int countTotalTransfersToBank(Long bankId);
}
