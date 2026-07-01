package org.example.session12.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.session12.entity.Account;
import org.example.session12.entity.AccountStatus;
import org.example.session12.entity.EkycSession;
import org.example.session12.enums.EkycStatus;
import org.example.session12.exception.BusinessException;
import org.example.session12.exception.ResourceNotFoundException;
import org.example.session12.repository.AccountRepository;
import org.example.session12.repository.EkycSessionRepository;
import org.example.session12.service.ManualReviewService;
import org.example.session12.service.StpEvaluationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ManualReviewServiceImpl implements ManualReviewService {

    private final EkycSessionRepository sessionRepository;
    private final AccountRepository accountRepository;
    private final StpEvaluationService stpEvaluationService;

    @Override
    public List<EkycSession> getPendingReviewQueue() {
        return sessionRepository.findByStatus(EkycStatus.PENDING_MANUAL_REVIEW);
    }

    @Override
    public EkycSession getSessionDetail(String sessionId) {
        return sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ eKYC với mã: " + sessionId));
    }

    @Override
    @Transactional
    public void approveSession(String sessionId, String reviewerId) {
        EkycSession session = getSessionDetail(sessionId);

        if (!EkycStatus.PENDING_MANUAL_REVIEW.equals(session.getStatus())) {
            throw new BusinessException("INVALID_STATUS",
                    "Hồ sơ không ở trạng thái chờ phê duyệt thủ công.");
        }

        String accountNumber = stpEvaluationService.generateAccountNumber();
        String cif = stpEvaluationService.generateCif();

        Account account = Account.builder()
                .accountId(UUID.randomUUID().toString())
                .fullName(session.getPhone())
                .phone(session.getPhone())
                .email(session.getEmail())
                .citizenId("")
                .accountNumber(accountNumber)
                .status(AccountStatus.ACTIVE)
                .transactionLimit(new BigDecimal("100000000"))
                .ekycMethod("MANUAL_REVIEW")
                .build();

        accountRepository.save(account);

        session.setStatus(EkycStatus.MANUALLY_APPROVED);
        session.setAccount(account);
        session.setCif(cif);
        session.setManualReviewedBy(reviewerId);
        session.setManualReviewedAt(LocalDateTime.now());
        sessionRepository.save(session);

        System.out.println("[SMS SIMULATION] Account opened for " + session.getPhone()
                + " - Account: " + accountNumber + " - CIF: " + cif);
    }

    @Override
    @Transactional
    public void rejectSession(String sessionId, String reviewerId, String reason) {
        EkycSession session = getSessionDetail(sessionId);

        if (!EkycStatus.PENDING_MANUAL_REVIEW.equals(session.getStatus())) {
            throw new BusinessException("INVALID_STATUS",
                    "Hồ sơ không ở trạng thái chờ phê duyệt thủ công.");
        }

        session.setStatus(EkycStatus.REJECTED);
        session.setManualReviewedBy(reviewerId);
        session.setManualReviewedAt(LocalDateTime.now());
        session.setRejectionReason(reason);
        sessionRepository.save(session);

        System.out.println("[SMS SIMULATION] Account application rejected for "
                + session.getPhone() + " - Reason: " + reason);
    }
}
