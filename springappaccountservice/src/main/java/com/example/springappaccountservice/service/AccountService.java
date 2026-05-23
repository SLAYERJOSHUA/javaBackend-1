package com.example.springappaccountservice.service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.springappaccountservice.model.Account;
import com.example.springappaccountservice.model.AccountStatus;
import com.example.springappaccountservice.repository.AccountRepository;

@Service
public class AccountService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account createAccount(Account account) {
        account.setAccountId(null);
        account.setAccountNumber(generateUniqueAccountNumber());
        if (account.getStatus() == null) {
            account.setStatus(AccountStatus.ACTIVE);
        }
        if (account.getBalance() == null) {
            account.setBalance(BigDecimal.ZERO);
        }
        account.setPinGenerated(false);
        account.setPinHash(null);
        return accountRepository.save(account);
    }

    public Optional<Account> getAccountById(Long accountId) {
        return accountRepository.findById(accountId);
    }

    public Optional<Account> updateAccount(Long accountId, Account accountDetails) {
        return accountRepository.findById(accountId)
                .map(account -> {
                    if (accountDetails.getAccountType() != null) {
                        account.setAccountType(accountDetails.getAccountType());
                    }
                    if (accountDetails.getBalance() != null) {
                        account.setBalance(accountDetails.getBalance());
                    }
                    if (accountDetails.getUserId() != null) {
                        account.setUserId(accountDetails.getUserId());
                    }
                    if (accountDetails.getStatus() != null) {
                        account.setStatus(accountDetails.getStatus());
                    }
                    return accountRepository.save(account);
                });
    }

    public Page<Account> getAllAccounts(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

    public List<Account> getAccountsByUser(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    public List<Account> getActiveAccounts() {
        return accountRepository.findActiveAccounts();
    }

    public List<Account> getActiveAccountsByUser(Long userId) {
        return accountRepository.findByUserIdAndStatus(userId, AccountStatus.ACTIVE);
    }

    public Optional<BigDecimal> getAccountBalance(Long accountId) {
        return accountRepository.findById(accountId).map(Account::getBalance);
    }

    public Optional<Account> updateAccountBalance(Long accountId, BigDecimal balance) {
        return accountRepository.findById(accountId)
                .map(account -> {
                    account.setBalance(balance);
                    return accountRepository.save(account);
                });
    }

    public Optional<String> generatePin(Long accountId) {
        return accountRepository.findById(accountId)
                .map(account -> {
                    String pin = String.format("%04d", RANDOM.nextInt(10_000));
                    account.setPinHash(hashPin(pin));
                    account.setPinGenerated(true);
                    accountRepository.save(account);
                    return pin;
                });
    }

    public Optional<Boolean> setPin(Long accountId, String pin) {
        return accountRepository.findById(accountId)
                .map(account -> {
                    validatePin(pin);
                    account.setPinHash(hashPin(pin));
                    account.setPinGenerated(true);
                    accountRepository.save(account);
                    return true;
                });
    }

    public Optional<Boolean> verifyPin(Long accountId, String pin) {
        return accountRepository.findById(accountId)
                .map(account -> account.getPinHash() != null && account.getPinHash().equals(hashPin(pin)));
    }

    public Optional<Boolean> resetPin(Long accountId) {
        return accountRepository.findById(accountId)
                .map(account -> {
                    account.setPinHash(null);
                    account.setPinGenerated(false);
                    accountRepository.save(account);
                    return true;
                });
    }

    private String generateUniqueAccountNumber() {
        String accountNumber;
        do {
            long value = Math.abs(RANDOM.nextLong()) % 1_000_000_000_000L;
            accountNumber = String.format("%012d", value);
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }

    private void validatePin(String pin) {
        if (pin == null || !pin.matches("\\d{4,6}")) {
            throw new IllegalArgumentException("pin must be 4 to 6 digits");
        }
    }

    private String hashPin(String pin) {
        validatePin(pin);
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(pin.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }
}
