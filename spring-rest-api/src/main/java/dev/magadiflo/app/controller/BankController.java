package dev.magadiflo.app.controller;

import dev.magadiflo.app.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/banks")
public class BankController {

    private final AccountService accountService;

    @GetMapping(path = "/{bankId}")
    public ResponseEntity<Integer> countTotalTransfersToBank(@PathVariable Long bankId) {
        return ResponseEntity.ok(this.accountService.countTotalTransfersToBank(bankId));
    }

}
