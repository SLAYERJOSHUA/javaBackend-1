package com.example.springappaccountservice.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.springappaccountservice.model.AccountRequest;
import com.example.springappaccountservice.model.AccountType;
import com.example.springappaccountservice.service.AccountRequestService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping({"/api/account-requests", "/api/accounts/requests"})
public class AccountRequestController {

    private final AccountRequestService accountRequestService;

    public AccountRequestController(AccountRequestService accountRequestService) {
        this.accountRequestService = accountRequestService;
    }

    @PostMapping
    public ResponseEntity<AccountRequest> submitAccountRequest(@Valid @RequestBody SubmitAccountRequest request) {
        AccountRequest createdRequest = accountRequestService.submitAccountRequest(
                request.userId(),
                request.accountType(),
                request.initialDeposit(),
                request.accountPurpose(),
                request.documentPath(),
                request.documentName());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRequest);
    }

    @GetMapping("/my-requests")
    public ResponseEntity<List<AccountRequest>> getOwnAccountRequests(@RequestParam Long userId) {
        return ResponseEntity.ok(accountRequestService.getUserAccountRequests(userId));
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Boolean>> getAccountRequestStatus(@RequestParam Long userId) {
        return ResponseEntity.ok(Map.of(
                "hasPendingRequest", accountRequestService.hasPendingRequest(userId),
                "hasApprovedAccount", accountRequestService.hasApprovedAccount(userId)));
    }

    @PostMapping("/{requestId}/approve")
    public ResponseEntity<AccountRequest> approveAccountRequest(
            @PathVariable Long requestId,
            @Valid @RequestBody ReviewAccountRequest request) {
        return accountRequestService.approveAccountRequest(requestId, request.reviewerId(), request.reviewNotes())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{requestId}/reject")
    public ResponseEntity<AccountRequest> rejectAccountRequest(
            @PathVariable Long requestId,
            @Valid @RequestBody ReviewAccountRequest request) {
        return accountRequestService.rejectAccountRequest(requestId, request.reviewerId(), request.reviewNotes())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccountRequest>> getUserAccountRequests(@PathVariable Long userId) {
        return ResponseEntity.ok(accountRequestService.getUserAccountRequests(userId));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<AccountRequest>> getPendingAccountRequests() {
        return ResponseEntity.ok(accountRequestService.getPendingAccountRequests());
    }

    @GetMapping
    public ResponseEntity<List<AccountRequest>> getAllAccountRequests() {
        return ResponseEntity.ok(accountRequestService.getAllAccountRequests());
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<AccountRequest> getAccountRequestById(@PathVariable Long requestId) {
        return accountRequestService.getAccountRequestById(requestId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}/has-pending")
    public ResponseEntity<Map<String, Boolean>> hasPendingRequest(@PathVariable Long userId) {
        return ResponseEntity.ok(Map.of("hasPendingRequest", accountRequestService.hasPendingRequest(userId)));
    }

    @GetMapping("/user/{userId}/has-approved-account")
    public ResponseEntity<Map<String, Boolean>> hasApprovedAccount(@PathVariable Long userId) {
        return ResponseEntity.ok(Map.of("hasApprovedAccount", accountRequestService.hasApprovedAccount(userId)));
    }

    public record SubmitAccountRequest(
            @NotNull(message = "userId is required") Long userId,
            @NotNull(message = "accountType is required") AccountType accountType,
            @DecimalMin(value = "0.00", message = "initialDeposit cannot be negative")
            @Digits(integer = 13, fraction = 2, message = "initialDeposit must use precision 15,2")
            BigDecimal initialDeposit,
            @Size(max = 500, message = "accountPurpose must be at most 500 characters") String accountPurpose,
            String documentPath,
            String documentName) {
    }

    public record ReviewAccountRequest(
            @NotNull(message = "reviewerId is required") Long reviewerId,
            @Size(max = 1000, message = "reviewNotes must be at most 1000 characters") String reviewNotes) {
    }
}
