package org.example.session12.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.session12.dto.request.OtpSendRequest;
import org.example.session12.dto.request.OtpVerifyRequest;
import org.example.session12.dto.response.OtpResponse;
import org.example.session12.service.OtpService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/ekyc/otp")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;

    @PostMapping("/send")
    public ResponseEntity<OtpResponse> sendOtp(@Valid @RequestBody OtpSendRequest request) {
        OtpResponse response = otpService.sendOtp(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
        boolean verified = otpService.verifyOtp(request);
        return ResponseEntity.ok(Map.of(
                "verified", verified,
                "sessionId", request.getSessionId(),
                "message", "Xác thực OTP thành công."
        ));
    }
}
