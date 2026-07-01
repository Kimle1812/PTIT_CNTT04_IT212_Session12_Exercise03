package org.example.session12.service;

import org.example.session12.dto.response.AccountRegisterResponse;
import org.example.session12.entity.EkycSession;

public interface StpEvaluationService {
    boolean isEligibleForStp(EkycSession session);
    AccountRegisterResponse processStp(EkycSession session);
    String generateAccountNumber();
    String generateCif();
}
