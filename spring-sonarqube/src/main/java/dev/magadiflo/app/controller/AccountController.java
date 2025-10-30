package dev.magadiflo.app.controller;

import dev.magadiflo.app.dto.AccountCreateRequest;
import dev.magadiflo.app.dto.AccountResponse;
import dev.magadiflo.app.dto.AccountUpdateRequest;
import dev.magadiflo.app.dto.DepositRequest;
import dev.magadiflo.app.dto.TransactionRequest;
import dev.magadiflo.app.dto.WithdrawalRequest;
import dev.magadiflo.app.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<List<AccountResponse>> findAllAccounts() {
        return ResponseEntity.ok(this.accountService.findAllAccounts());
    }

    @GetMapping(path = "/{accountId}")
    public ResponseEntity<AccountResponse> findAccountById(@PathVariable Long accountId) {
        return ResponseEntity.ok(this.accountService.findAccountById(accountId));
    }

    @GetMapping(path = "/search")
    public ResponseEntity<AccountResponse> searchByHolder(@RequestParam String holder) {
        return ResponseEntity.ok(this.accountService.findAccountByHolder(holder));
    }

    @GetMapping(path = "/{accountId}/balance")
    public ResponseEntity<BigDecimal> getAccountBalance(@PathVariable Long accountId) {
        return ResponseEntity.ok(this.accountService.getAccountBalance(accountId));
    }

    @PostMapping
    public ResponseEntity<AccountResponse> saveAccount(@Valid @RequestBody AccountCreateRequest request) {
        AccountResponse account = this.accountService.saveAccount(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{accountId}")
                .buildAndExpand(account.id())
                .toUri();
        return ResponseEntity.created(location).body(account);
    }

    @PutMapping(path = "/{accountId}")
    public ResponseEntity<AccountResponse> updateAccount(@PathVariable Long accountId, @Valid @RequestBody AccountUpdateRequest request) {
        return ResponseEntity.ok(this.accountService.updateAccount(accountId, request));
    }

    @DeleteMapping(path = "/{accountId}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long accountId) {
        this.accountService.deleteAccount(accountId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/{accountId}/deposit")
    public ResponseEntity<AccountResponse> deposit(@PathVariable Long accountId, @Valid @RequestBody DepositRequest request) {
        return ResponseEntity.ok(this.accountService.deposit(accountId, request));
    }

    @PostMapping(path = "/{accountId}/withdraw")
    public ResponseEntity<AccountResponse> withdraw(@PathVariable Long accountId, @Valid @RequestBody WithdrawalRequest request) {
        return ResponseEntity.ok(this.accountService.withdraw(accountId, request));
    }

    @PostMapping(path = "/transfer")
    public ResponseEntity<Void> transfer(@Valid @RequestBody TransactionRequest request) {
        this.accountService.transfer(request);
        return ResponseEntity.noContent().build();
    }
}
