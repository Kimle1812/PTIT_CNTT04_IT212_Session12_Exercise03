package org.example.session12.service;

import org.example.session12.dto.request.OtpSendRequest;
import org.example.session12.dto.request.OtpVerifyRequest;
import org.example.session12.dto.response.OtpResponse;

public interface OtpService {
    OtpResponse sendOtp(OtpSendRequest request);
    boolean verifyOtp(OtpVerifyRequest request);
}
