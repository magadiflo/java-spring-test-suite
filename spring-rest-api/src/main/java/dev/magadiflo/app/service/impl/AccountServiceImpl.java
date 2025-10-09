package dev.magadiflo.app.service.impl;

import dev.magadiflo.app.dto.AccountCreateRequest;
import dev.magadiflo.app.dto.AccountResponse;
import dev.magadiflo.app.dto.AccountUpdateRequest;
import dev.magadiflo.app.dto.DepositRequest;
import dev.magadiflo.app.dto.TransactionRequest;
import dev.magadiflo.app.dto.WithdrawalRequest;
import dev.magadiflo.app.entity.Account;
import dev.magadiflo.app.entity.Bank;
import dev.magadiflo.app.exception.*;
import dev.magadiflo.app.mapper.AccountMapper;
import dev.magadiflo.app.repository.AccountRepository;
import dev.magadiflo.app.repository.BankRepository;
import dev.magadiflo.app.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final BankRepository bankRepository;
    private final AccountMapper accountMapper;

    @Override
    public List<AccountResponse> findAllAccounts() {
        log.debug("Consultando todas las cuentas");
        List<AccountResponse> accounts = this.accountRepository.getAllAccounts();
        log.info("Se encontraron {} cuentas", accounts.size());
        return accounts;
    }

    @Override
    public AccountResponse findAccountById(Long accountId) {
        log.debug("Buscando cuenta con ID: {}", accountId);
        return this.accountRepository.findById(accountId)
                .map(account -> {
                    log.info("Cuenta encontrada | ID: {} | Titular: {}", accountId, account.getHolder());
                    return this.accountMapper.toAccountResponse(account);
                })
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    @Override
    public AccountResponse findAccountByHolder(String holder) {
        log.debug("Buscando cuenta del titular: {}", holder);
        return this.accountRepository.findAccountByHolder(holder)
                .map(account -> {
                    log.info("Cuenta encontrada | Titular: {} | ID: {}", account.getHolder(), account.getId());
                    return this.accountMapper.toAccountResponse(account);
                })
                .orElseThrow(() -> new AccountNotFoundException(holder));
    }

    @Override
    public BigDecimal getAccountBalance(Long accountId) {
        log.debug("Consultando saldo de la cuenta con ID: {}", accountId);
        return this.accountRepository.findById(accountId)
                .map(account -> {
                    log.info("Saldo consultado | Cuenta ID: {} | Saldo: {}", accountId, account.getBalance());
                    return account.getBalance();
                })
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    @Override
    @Transactional
    public AccountResponse saveAccount(AccountCreateRequest accountRequest) {
        log.debug("Iniciando registro de cuenta para el titular: {}", accountRequest.holder());

        Bank bank = this.bankRepository.findById(accountRequest.bankId())
                .orElseThrow(() -> new BankNotFoundException(accountRequest.bankId()));

        Account account = this.accountMapper.toAccount(accountRequest, bank);

        this.accountRepository.save(account);

        log.info("Cuenta registrada exitosamente | ID: {} | Titular: {} | Banco: {} | Saldo inicial: {}",
                account.getId(), account.getHolder(), bank.getName(), account.getBalance());
        return this.accountMapper.toAccountResponse(account);
    }

    @Override
    @Transactional
    public AccountResponse updateAccount(Long accountId, AccountUpdateRequest accountRequest) {
        log.debug("Iniciando actualización del titular para la cuenta con ID: {}", accountId);
        return this.accountRepository.findById(accountId)
                .map(account -> {
                    log.info("Cuenta encontrada | ID: {} | Titular actual: {}", accountId, account.getHolder());
                    return this.accountMapper.toUpdateAccount(accountRequest, account);
                })
                .map(this.accountRepository::save)
                .map(account -> {
                    log.info("Cuenta actualizada | ID: {} | Nuevo titular: {}", accountId, account.getHolder());
                    return this.accountMapper.toAccountResponse(account);
                })
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    @Override
    @Transactional
    public void deleteAccount(Long accountId) {
        log.debug("Iniciando eliminación de la cuenta con ID: {}", accountId);
        this.accountRepository.findById(accountId)
                .map(account -> this.accountRepository.deleteAccountById(account.getId()))
                .map(affectedRows -> {
                    if (affectedRows == 0) {
                        log.error("No se pudo eliminar la cuenta con ID: {}", accountId);
                        throw new DatabaseOperationException("DELETE cuenta");
                    }
                    log.info("Cuenta eliminada exitosamente | ID: {}", accountId);
                    return affectedRows;
                })
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    @Override
    @Transactional
    public AccountResponse deposit(Long accountId, DepositRequest request) {
        log.debug("Iniciando depósito a la cuenta con ID: {}, monto: {}", accountId, request.amount());
        return this.accountRepository.findById(accountId)
                .map(account -> this.makeADeposit(account, request.amount()))
                .map(this.accountRepository::save)
                .map(account -> {
                    log.info("Depósito exitoso | Cuenta ID: {} | Monto depositado: {} | Nuevo saldo: {}",
                            account.getId(), request.amount(), account.getBalance());
                    return this.accountMapper.toAccountResponse(account);
                })
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    @Override
    @Transactional
    public AccountResponse withdraw(Long accountId, WithdrawalRequest request) {
        log.debug("Iniciando retiro de la cuenta con ID: {}, monto: {}", accountId, request.amount());
        return this.accountRepository.findById(accountId)
                .map(account -> this.makeAWithdrawal(account, request.amount()))
                .map(this.accountRepository::save)
                .map(account -> {
                    log.info("Retiro exitoso | Cuenta ID: {} | Monto retirado: {} | Nuevo saldo: {}",
                            account.getId(), request.amount(), account.getBalance());
                    return this.accountMapper.toAccountResponse(account);
                })
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    @Override
    @Transactional
    public void transfer(TransactionRequest request) {
        log.debug("Iniciando transferencia | Origen: {} | Destino: {} | Monto: {}",
                request.sourceAccountId(), request.targetAccountId(), request.amount());

        if (request.sourceAccountId().equals(request.targetAccountId())) {
            log.warn("Intento de transferencia a la misma cuenta: {}", request.sourceAccountId());
            throw new InvalidTransactionException("No se puede hacer transferencia de una cuenta a sí misma");
        }

        Account sourceAccount = this.accountRepository.findById(request.sourceAccountId())
                .orElseThrow(() -> new AccountNotFoundException(request.sourceAccountId()));
        Account targetAccount = this.accountRepository.findById(request.targetAccountId())
                .orElseThrow(() -> new AccountNotFoundException(request.targetAccountId()));

        if (!sourceAccount.getBank().getId().equals(targetAccount.getBank().getId())) {
            log.warn("Intento de transferencia entre bancos diferentes | Banco origen: {} | Banco destino: {}",
                    sourceAccount.getBank().getName(), targetAccount.getBank().getName());
            throw new InvalidTransactionException("No se puede hacer transferencia entre cuentas de diferentes bancos");
        }

        Bank bank = sourceAccount.getBank();
        bank.setTotalTransfers(bank.getTotalTransfers() + 1);

        // Aunque las entidades Account y Bank están en estado MANAGED dentro de esta transacción,
        // usamos save(...) explícitamente para reforzar la intención de persistencia,
        // facilitar la trazabilidad del flujo y permitir verificación en tests unitarios.
        // JPA sincronizará los cambios al final del method, pero estos save(...) hacen visible el punto
        // de persistencia.
        this.accountRepository.save(this.makeAWithdrawal(sourceAccount, request.amount()));
        this.accountRepository.save(this.makeADeposit(targetAccount, request.amount()));
        this.bankRepository.save(bank);

        log.info("Transferencia exitosa | De: {} (ID: {}) | Para: {} (ID: {}) | Monto: {} | Banco: {} | Total transferencias: {}",
                sourceAccount.getHolder(), sourceAccount.getId(),
                targetAccount.getHolder(), targetAccount.getId(),
                request.amount(), bank.getName(), bank.getTotalTransfers());
    }

    @Override
    public int countTotalTransfersToBank(Long bankId) {
        log.debug("Consultando total de transferencias del banco con ID: {}", bankId);
        return this.bankRepository.findById(bankId)
                .map(bank -> {
                    log.info("Total de transferencias del banco {} (ID: {}): {}",
                            bank.getName(), bank.getId(), bank.getTotalTransfers());
                    return bank.getTotalTransfers();
                })
                .orElseThrow(() -> new BankNotFoundException(bankId));
    }

    private Account makeADeposit(Account account, BigDecimal amount) {
        log.info("Aplicando depósito a cuenta con ID: {}, saldo actual: {}, monto a agregar: {}",
                account.getId(), account.getBalance(), amount);

        account.setBalance(account.getBalance().add(amount));
        return account;
    }

    private Account makeAWithdrawal(Account account, BigDecimal amount) {
        log.info("Validando el retiro de saldo para la cuenta con ID: {}, saldo actual: {}, monto a retirar: {}",
                account.getId(), account.getBalance(), amount);

        if (amount.compareTo(account.getBalance()) > 0) {
            log.warn("Solicitud rechazada por saldo insuficiente | Cuenta ID: {} | Titular: {} | Saldo: {} | Monto solicitado: {}",
                    account.getId(), account.getHolder(), account.getBalance(), amount);
            throw new InsufficientBalanceException(account.getId(), account.getHolder());
        }

        account.setBalance(account.getBalance().subtract(amount));
        return account;
    }
}
