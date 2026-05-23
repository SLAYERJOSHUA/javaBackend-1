package com.example.springappaccountservice.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.springappaccountservice.model.Account;
import com.example.springappaccountservice.model.AccountRequest;
import com.example.springappaccountservice.model.AccountStatus;
import com.example.springappaccountservice.model.AccountType;
import com.example.springappaccountservice.model.RequestStatus;
import com.example.springappaccountservice.repository.AccountRepository;
import com.example.springappaccountservice.repository.AccountRequestRepository;

@Service
public class AccountRequestService {

    private final AccountRequestRepository accountRequestRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final SimpMessagingTemplate messagingTemplate;

    public AccountRequestService(
            AccountRequestRepository accountRequestRepository,
            AccountRepository accountRepository,
            AccountService accountService,
            SimpMessagingTemplate messagingTemplate) {
        this.accountRequestRepository = accountRequestRepository;
        this.accountRepository = accountRepository;
        this.accountService = accountService;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public AccountRequest submitAccountRequest(
            Long userId,
            AccountType accountType,
            BigDecimal initialDeposit,
            String accountPurpose,
            String documentPath,
            String documentName) {
        if (hasPendingRequest(userId)) {
            throw new IllegalArgumentException("User already has a pending account request");
        }

        AccountRequest request = new AccountRequest();
        request.setUserId(userId);
        request.setAccountType(accountType);
        request.setInitialDeposit(initialDeposit == null ? BigDecimal.ZERO : initialDeposit);
        request.setAccountPurpose(accountPurpose);
        request.setDocumentPath(documentPath);
        request.setDocumentName(documentName);
        request.setStatus(RequestStatus.PENDING);

        AccountRequest savedRequest = accountRequestRepository.save(request);
        messagingTemplate.convertAndSend("/topic/admin/account-requests", savedRequest);
        return savedRequest;
    }

    @Transactional
    public Optional<AccountRequest> approveAccountRequest(Long requestId, Long reviewerId, String reviewNotes) {
        return accountRequestRepository.findById(requestId)
                .map(request -> {
                    ensurePending(request);

                    Account account = new Account();
                    account.setUserId(request.getUserId());
                    account.setAccountType(request.getAccountType());
                    account.setBalance(request.getInitialDeposit());
                    account.setStatus(AccountStatus.ACTIVE);

                    Account approvedAccount = accountService.createAccount(account);
                    request.setStatus(RequestStatus.APPROVED);
                    request.setReviewedBy(reviewerId);
                    request.setReviewNotes(reviewNotes);
                    request.setReviewedAt(LocalDateTime.now());
                    request.setApprovedAccountId(approvedAccount.getAccountId());

                    AccountRequest savedRequest = accountRequestRepository.save(request);
                    messagingTemplate.convertAndSend("/topic/user/" + request.getUserId() + "/account-requests", savedRequest);
                    return savedRequest;
                });
    }

    @Transactional
    public Optional<AccountRequest> rejectAccountRequest(Long requestId, Long reviewerId, String reviewNotes) {
        return accountRequestRepository.findById(requestId)
                .map(request -> {
                    ensurePending(request);
                    request.setStatus(RequestStatus.REJECTED);
                    request.setReviewedBy(reviewerId);
                    request.setReviewNotes(reviewNotes);
                    request.setReviewedAt(LocalDateTime.now());

                    AccountRequest savedRequest = accountRequestRepository.save(request);
                    messagingTemplate.convertAndSend("/topic/user/" + request.getUserId() + "/account-requests", savedRequest);
                    return savedRequest;
                });
    }

    public List<AccountRequest> getUserAccountRequests(Long userId) {
        return accountRequestRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<AccountRequest> getPendingAccountRequests() {
        return accountRequestRepository.findByStatus(RequestStatus.PENDING);
    }

    public List<AccountRequest> getAllAccountRequests() {
        return accountRequestRepository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<AccountRequest> getAccountRequestById(Long requestId) {
        return accountRequestRepository.findById(requestId);
    }

    public boolean hasPendingRequest(Long userId) {
        return accountRequestRepository.existsByUserIdAndStatus(userId, RequestStatus.PENDING);
    }

    public boolean hasApprovedAccount(Long userId) {
        return accountRepository.existsByUserIdAndStatus(userId, AccountStatus.ACTIVE);
    }

    private void ensurePending(AccountRequest request) {
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalArgumentException("Only pending account requests can be reviewed");
        }
    }
}
