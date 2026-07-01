package org.example.session12.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.session12.dto.response.AccountRegisterResponse;
import org.example.session12.entity.Account;
import org.example.session12.entity.AccountStatus;
import org.example.session12.entity.EkycSession;
import org.example.session12.enums.AmlResult;
import org.example.session12.enums.EkycStatus;
import org.example.session12.enums.MatchResult;
import org.example.session12.enums.TransactionLimit;
import org.example.session12.exception.BusinessException;
import org.example.session12.repository.AccountRepository;
import org.example.session12.repository.EkycSessionRepository;
import org.example.session12.service.StpEvaluationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StpEvaluationServiceImpl implements StpEvaluationService {

    private final AccountRepository accountRepository;
    private final EkycSessionRepository sessionRepository;
    private static final SecureRandom RANDOM = new SecureRandom();

    private static final double STP_MATCH_THRESHOLD = 0.85;
    private static final double MANUAL_REVIEW_MIN_THRESHOLD = 0.70;
    private static final int MIN_AGE = 18;
    private static final int MAX_FACE_SCAN_ATTEMPTS = 3;
    private static final int FACE_SCAN_LOCK_HOURS = 24;

    @Override
    public boolean isEligibleForStp(EkycSession session) {
        if (session == null) return false;

        if (!EkycStatus.AML_CLEARED.equals(session.getStatus())
                && !EkycStatus.NFC_VERIFIED.equals(session.getStatus())) {
            return false;
        }

        if (!"PASS".equals(session.getLivenessResult())) {
            return false;
        }

        Double matchScore = session.getFaceMatchScore();
        if (matchScore == null || matchScore < STP_MATCH_THRESHOLD) {
            return false;
        }

        if (!"CLEAN".equals(session.getAmlResult())) {
            return false;
        }

        return true;
    }

    @Override
    @Transactional
    public AccountRegisterResponse processStp(EkycSession session) {
        if (!isEligibleForStp(session)) {
            throw new BusinessException("STP_NOT_ELIGIBLE",
                    "Hồ sơ chưa đủ điều kiện phê duyệt tự động.");
        }

        String cif = generateCif();
        String accountNumber = generateAccountNumber();

        BigDecimal transactionLimit = Boolean.TRUE.equals(session.getNfcVerified())
                ? TransactionLimit.UNLIMITED.getLimit()
                : TransactionLimit.STANDARD_EKYC.getLimit();

        Account account = Account.builder()
                .accountId(UUID.randomUUID().toString())
                .fullName(session.getPhone())
                .phone(session.getPhone())
                .email(session.getEmail())
                .citizenId("")
                .accountNumber(accountNumber)
                .status(AccountStatus.ACTIVE)
                .transactionLimit(transactionLimit)
                .ekycMethod(Boolean.TRUE.equals(session.getNfcVerified()) ? "NFC" : "STANDARD")
                .build();

        Account saved = accountRepository.save(account);

        session.setStatus(EkycStatus.STP_APPROVED);
        session.setAccount(saved);
        session.setCif(cif);
        sessionRepository.save(session);

        return AccountRegisterResponse.builder()
                .accountId(saved.getAccountId())
                .accountNumber(saved.getAccountNumber())
                .status(saved.getStatus().name())
                .build();
    }

    @Override
    public String generateAccountNumber() {
        String accountNumber;
        do {
            long num = 999_000_000_000L + (long) (RANDOM.nextDouble() * 999_000_000_000L);
            accountNumber = String.valueOf(num);
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }

    @Override
    public String generateCif() {
        return "CIF" + String.format("%010d", Math.abs(RANDOM.nextLong()) % 9999999999L);
    }

    public boolean checkAgeRequirement(LocalDate dateOfBirth) {
        if (dateOfBirth == null) return false;
        int age = Period.between(dateOfBirth, LocalDate.now()).getYears();
        return age >= MIN_AGE;
    }

    public MatchResult classifyMatchScore(double score) {
        if (score >= STP_MATCH_THRESHOLD) {
            return MatchResult.APPROVED;
        } else if (score >= MANUAL_REVIEW_MIN_THRESHOLD) {
            return MatchResult.WARNING;
        } else {
            return MatchResult.REJECTED;
        }
    }

    public void checkFaceScanLimit(EkycSession session) {
        if (session.getFaceScanLockedUntil() != null
                && session.getFaceScanLockedUntil().isAfter(LocalDateTime.now())) {
            long remainingHours = java.time.Duration.between(
                    LocalDateTime.now(), session.getFaceScanLockedUntil()).toHours();
            throw new BusinessException("FACE_SCAN_LOCKED",
                    "Chức năng quét khuôn mặt đã bị khóa. Vui lòng thử lại sau "
                            + remainingHours + " giờ.");
        }

        if (session.getFaceScanAttempts() >= MAX_FACE_SCAN_ATTEMPTS) {
            session.setFaceScanLockedUntil(LocalDateTime.now().plusHours(FACE_SCAN_LOCK_HOURS));
            sessionRepository.save(session);
            throw new BusinessException("FACE_SCAN_LOCKED",
                    "Bạn đã vượt quá số lần quét khuôn mặt cho phép. "
                            + "Vui lòng thử lại sau 24 giờ.");
        }
    }
}
