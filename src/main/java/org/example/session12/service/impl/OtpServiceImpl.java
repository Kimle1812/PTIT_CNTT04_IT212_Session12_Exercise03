package org.example.session12.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.session12.dto.request.OtpSendRequest;
import org.example.session12.dto.request.OtpVerifyRequest;
import org.example.session12.dto.response.OtpResponse;
import org.example.session12.entity.EkycSession;
import org.example.session12.enums.EkycStatus;
import org.example.session12.exception.BusinessException;
import org.example.session12.repository.AccountRepository;
import org.example.session12.repository.EkycSessionRepository;
import org.example.session12.service.OtpService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final AccountRepository accountRepository;
    private final EkycSessionRepository sessionRepository;

    private static final int OTP_EXPIRY_SECONDS = 120;
    private static final int MAX_OTP_ATTEMPTS = 3;
    private static final int OTP_LOCK_MINUTES = 15;

    private final Map<String, OtpCacheEntry> otpCache = new ConcurrentHashMap<>();

    private static class OtpCacheEntry {
        final String otpCode;
        final LocalDateTime expiresAt;
        int attemptCount;
        LocalDateTime lockedUntil;

        OtpCacheEntry(String otpCode, LocalDateTime expiresAt) {
            this.otpCode = otpCode;
            this.expiresAt = expiresAt;
            this.attemptCount = 0;
            this.lockedUntil = null;
        }
    }

    @Override
    @Transactional
    public OtpResponse sendOtp(OtpSendRequest request) {
        if (accountRepository.existsByPhone(request.getPhone())) {
            throw new BusinessException("PHONE_EXISTS",
                    "Số điện thoại này đã được đăng ký dịch vụ tại ABC Bank. Vui lòng đăng nhập hoặc sử dụng tính năng Quên mật khẩu");
        }

        String cacheKey = generateCacheKey(request.getPhone());
        OtpCacheEntry existing = otpCache.get(cacheKey);

        if (existing != null && existing.lockedUntil != null && existing.lockedUntil.isAfter(LocalDateTime.now())) {
            long remainingMinutes = java.time.Duration.between(LocalDateTime.now(), existing.lockedUntil).toMinutes();
            throw new BusinessException("OTP_LOCKED",
                    "Yêu cầu OTP đã bị tạm khóa. Vui lòng thử lại sau " + remainingMinutes + " phút.");
        }

        String otpCode = String.format("%06d", new SecureRandom().nextInt(999999));
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(OTP_EXPIRY_SECONDS);

        otpCache.put(cacheKey, new OtpCacheEntry(otpCode, expiresAt));

        String sessionId = UUID.randomUUID().toString();

        EkycSession session = EkycSession.builder()
                .sessionId(sessionId)
                .phone(request.getPhone())
                .email(request.getEmail())
                .status(EkycStatus.INITIATED)
                .otpSentAt(LocalDateTime.now())
                .build();
        sessionRepository.save(session);

        System.out.println("[SMS SIMULATION] OTP to " + request.getPhone() + ": " + otpCode);

        return OtpResponse.builder()
                .sessionId(sessionId)
                .expiresInSeconds(OTP_EXPIRY_SECONDS)
                .message("Mã OTP đã được gửi đến số điện thoại " + maskPhone(request.getPhone()))
                .build();
    }

    @Override
    @Transactional
    public boolean verifyOtp(OtpVerifyRequest request) {
        EkycSession session = sessionRepository.findBySessionId(request.getSessionId())
                .orElseThrow(() -> new BusinessException("SESSION_NOT_FOUND", "Phiên đăng ký không hợp lệ."));

        String cacheKey = generateCacheKey(session.getPhone());
        OtpCacheEntry entry = otpCache.get(cacheKey);

        if (entry == null) {
            throw new BusinessException("OTP_NOT_FOUND", "Mã OTP không tồn tại hoặc đã hết hạn.");
        }

        if (entry.lockedUntil != null && entry.lockedUntil.isAfter(LocalDateTime.now())) {
            throw new BusinessException("OTP_LOCKED", "Bạn đã nhập sai OTP quá nhiều lần. Vui lòng thử lại sau 15 phút.");
        }

        if (LocalDateTime.now().isAfter(entry.expiresAt)) {
            otpCache.remove(cacheKey);
            throw new BusinessException("OTP_EXPIRED", "Mã OTP đã hết hạn. Vui lòng yêu cầu gửi lại mã mới.");
        }

        if (entry.attemptCount >= MAX_OTP_ATTEMPTS) {
            entry.lockedUntil = LocalDateTime.now().plusMinutes(OTP_LOCK_MINUTES);
            throw new BusinessException("OTP_LOCKED", "Bạn đã nhập sai OTP quá nhiều lần. Vui lòng thử lại sau 15 phút.");
        }

        if (!entry.otpCode.equals(request.getOtpCode())) {
            entry.attemptCount++;
            session.setOtpAttempts(session.getOtpAttempts() + 1);
            sessionRepository.save(session);
            int remainingAttempts = MAX_OTP_ATTEMPTS - entry.attemptCount;
            throw new BusinessException("OTP_INVALID",
                    "Mã OTP không chính xác. Còn " + remainingAttempts + " lần nhập.");
        }

        otpCache.remove(cacheKey);
        session.setStatus(EkycStatus.OTP_VERIFIED);
        session.setOtpVerifiedAt(LocalDateTime.now());
        sessionRepository.save(session);
        return true;
    }

    private String generateCacheKey(String phone) {
        return "OTP_" + phone;
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) return phone;
        return phone.substring(0, 4) + "****" + phone.substring(phone.length() - 3);
    }
}
