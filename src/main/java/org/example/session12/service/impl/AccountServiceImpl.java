package org.example.session12.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.session12.dto.request.AccountRegisterRequest;
import org.example.session12.dto.response.AccountRegisterResponse;
import org.example.session12.entity.Account;
import org.example.session12.entity.AccountStatus;
import org.example.session12.exception.DuplicateResourceException;
import org.example.session12.repository.AccountRepository;
import org.example.session12.service.AccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    @Transactional
    public AccountRegisterResponse registerBasicAccount(AccountRegisterRequest request) {
        if (accountRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("Số điện thoại này đã được đăng ký sử dụng.");
        }

        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Địa chỉ email này đã được đăng ký sử dụng.");
        }

        if (accountRepository.existsByCitizenId(request.getCitizenId())) {
            throw new DuplicateResourceException("Số CCCD này đã được đăng ký cho một tài khoản khác.");
        }

        String accountNumber = generateAccountNumber();

        Account account = Account.builder()
                .accountId(UUID.randomUUID().toString())
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .citizenId(request.getCitizenId())
                .accountNumber(accountNumber)
                .status(AccountStatus.PENDING)
                .nationality("Viet Nam")
                .build();

        Account saved = accountRepository.save(account);

        return AccountRegisterResponse.builder()
                .accountId(saved.getAccountId())
                .accountNumber(saved.getAccountNumber())
                .status(saved.getStatus().name())
                .build();
    }

    private String generateAccountNumber() {
        String accountNumber;
        do {
            long num = 999_000_000_000L + (long) (RANDOM.nextDouble() * 999_000_000_000L);
            accountNumber = String.valueOf(num);
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }
}
