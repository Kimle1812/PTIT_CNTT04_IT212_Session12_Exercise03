package org.example.session12.controller;

import lombok.RequiredArgsConstructor;
import org.example.session12.dto.response.AccountRegisterResponse;
import org.example.session12.entity.EkycSession;
import org.example.session12.enums.EkycStatus;
import org.example.session12.enums.MatchResult;
import org.example.session12.exception.BusinessException;
import org.example.session12.repository.EkycSessionRepository;
import org.example.session12.service.AmlCheckService;
import org.example.session12.service.StpEvaluationService;
import org.example.session12.enums.AmlResult;

import java.util.Map;

import static org.example.session12.enums.MatchResult.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/ekyc/stp")
@RequiredArgsConstructor
public class StpController {

    private final StpEvaluationService stpEvaluationService;
    private final AmlCheckService amlCheckService;
    private final EkycSessionRepository sessionRepository;

    @PostMapping("/evaluate")
    public ResponseEntity<Map<String, Object>> evaluateStp(@RequestParam("sessionId") String sessionId) {
        EkycSession session = sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new BusinessException("SESSION_NOT_FOUND", "Không tìm thấy phiên eKYC."));

        Double matchScore = session.getFaceMatchScore();
        MatchResult matchResult = matchScore != null
                ? classifyMatchScore(matchScore)
                : MatchResult.REJECTED;

        boolean eligible = stpEvaluationService.isEligibleForStp(session);

        if (eligible) {
            AccountRegisterResponse response = stpEvaluationService.processStp(session);
            return ResponseEntity.ok(Map.of(
                    "eligible", true,
                    "status", "STP_APPROVED",
                    "accountNumber", response.getAccountNumber(),
                    "accountId", response.getAccountId(),
                    "message", "Tài khoản đã được mở thành công."
            ));
        }

        if (MatchResult.WARNING.equals(matchResult)) {
            session.setStatus(EkycStatus.PENDING_MANUAL_REVIEW);
            session.setManualReviewRequired(true);
            session.setManualReviewReason("Face Match Score: " + (matchScore != null ? matchScore : 0));
            sessionRepository.save(session);

            return ResponseEntity.ok(Map.of(
                    "eligible", false,
                    "status", "PENDING_MANUAL_REVIEW",
                    "message", "Hồ sơ đang được bộ phận chuyên trách rà soát. Kết quả sẽ được gửi qua SMS trong vòng 15 phút."
            ));
        }

        return ResponseEntity.ok(Map.of(
                "eligible", false,
                "status", "REJECTED",
                "message", "Hồ sơ không đủ điều kiện mở tài khoản trực tuyến. Vui lòng mang giấy tờ tùy thân gốc đến chi nhánh ABC Bank gần nhất."
        ));
    }

    private MatchResult classifyMatchScore(double score) {
        if (score >= 0.85) return APPROVED;
        if (score >= 0.70) return WARNING;
        return REJECTED;
    }

    @PostMapping("/aml-check")
    public ResponseEntity<Map<String, Object>> performAmlCheck(
            @RequestParam("sessionId") String sessionId,
            @RequestParam("fullName") String fullName,
            @RequestParam("citizenId") String citizenId) {

        EkycSession session = sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new BusinessException("SESSION_NOT_FOUND", "Không tìm thấy phiên eKYC."));

        var amlResult = amlCheckService.performAmlCheck(fullName, citizenId, session.getPhone());

        session.setAmlResult(amlResult.name());
            if (amlResult == AmlResult.CLEAN) {
            session.setStatus(EkycStatus.AML_CLEARED);
            } else if (amlResult == AmlResult.BLOCKED) {
            session.setStatus(EkycStatus.REJECTED);
        }
        sessionRepository.save(session);

        return ResponseEntity.ok(Map.of(
                "sessionId", sessionId,
                "amlResult", amlResult.name(),
                "details", amlCheckService.getAmlCheckDetails(fullName, citizenId)
        ));
    }
}
