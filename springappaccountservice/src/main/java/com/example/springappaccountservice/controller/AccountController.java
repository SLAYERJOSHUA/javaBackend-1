package com.example.springappaccountservice.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.springappaccountservice.model.Account;
import com.example.springappaccountservice.service.AccountService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<Account> createAccount(@Valid @RequestBody Account account) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(account));
    }

    @PostMapping("/self")
    public ResponseEntity<Account> createOwnAccount(@Valid @RequestBody CreateOwnAccountRequest request) {
        Account account = new Account();
        account.setAccountType(request.accountType());
        account.setBalance(request.initialDeposit() == null ? BigDecimal.ZERO : request.initialDeposit());
        account.setUserId(request.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(account));
    }

    @GetMapping
    public ResponseEntity<Page<Account>> getAllAccounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(accountService.getAllAccounts(PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        return accountService.getAccountById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Account> updateAccount(@PathVariable Long id, @Valid @RequestBody Account account) {
        return accountService.updateAccount(id, account)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Account>> getAccountsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(accountService.getAccountsByUser(userId));
    }

    @GetMapping("/active")
    public ResponseEntity<List<Account>> getActiveAccounts() {
        return ResponseEntity.ok(accountService.getActiveAccounts());
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<List<Account>> getActiveAccountsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(accountService.getActiveAccountsByUser(userId));
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<Map<String, BigDecimal>> getAccountBalance(@PathVariable Long id) {
        return accountService.getAccountBalance(id)
                .map(balance -> ResponseEntity.ok(Map.of("balance", balance)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/balance")
    public ResponseEntity<Account> updateAccountBalance(@PathVariable Long id, @RequestBody Map<String, BigDecimal> request) {
        return accountService.updateAccountBalance(id, request.get("balance"))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/generate-pin")
    public ResponseEntity<Map<String, String>> generatePin(@PathVariable Long id) {
        return accountService.generatePin(id)
                .map(pin -> ResponseEntity.ok(Map.of(
                        "message", "PIN generated successfully",
                        "pin", pin)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/set-pin")
    public ResponseEntity<Map<String, String>> setPin(@PathVariable Long id, @RequestBody Map<String, String> request) {
        return accountService.setPin(id, request.get("pin"))
                .map(success -> ResponseEntity.ok(Map.of("message", "PIN set successfully")))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/verify-pin")
    public ResponseEntity<Map<String, Object>> verifyPin(@PathVariable Long id, @RequestBody Map<String, String> request) {
        return accountService.verifyPin(id, request.get("pin"))
                .map(verified -> ResponseEntity.ok(Map.<String, Object>of(
                        "verified", verified,
                        "message", verified ? "PIN verified successfully" : "Invalid PIN")))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/reset-pin")
    public ResponseEntity<Map<String, String>> resetPin(@PathVariable Long id) {
        return accountService.resetPin(id)
                .map(success -> ResponseEntity.ok(Map.of("message", "PIN reset successfully")))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public record CreateOwnAccountRequest(
            com.example.springappaccountservice.model.AccountType accountType,
            Long userId,
            BigDecimal initialDeposit) {
    }
}
