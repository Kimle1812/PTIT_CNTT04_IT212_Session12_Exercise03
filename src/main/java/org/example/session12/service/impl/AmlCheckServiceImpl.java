package org.example.session12.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.session12.enums.AmlResult;
import org.example.session12.service.AmlCheckService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AmlCheckServiceImpl implements AmlCheckService {

    private static final Set<String> BLACKLISTED_IDS = new HashSet<>();
    private static final Set<String> PEP_IDS = new HashSet<>();

    static {
        BLACKLISTED_IDS.add("000000000000");
        BLACKLISTED_IDS.add("111111111111");
        PEP_IDS.add("999999999999");
    }

    @Override
    public AmlResult performAmlCheck(String fullName, String citizenId, String phone) {
        if (isBlacklisted(citizenId)) {
            return AmlResult.BLOCKED;
        }

        if (PEP_IDS.contains(citizenId)) {
            return AmlResult.WARNING;
        }

        double riskScore = new Random().nextDouble();
        if (riskScore < 0.01) {
            return AmlResult.WARNING;
        }

        return AmlResult.CLEAN;
    }

    @Override
    public boolean isBlacklisted(String citizenId) {
        return BLACKLISTED_IDS.contains(citizenId);
    }

    @Override
    public Map<String, Object> getAmlCheckDetails(String fullName, String citizenId) {
        Map<String, Object> details = new HashMap<>();
        details.put("fullName", fullName);
        details.put("citizenId", maskCitizenId(citizenId));
        details.put("amlResult", performAmlCheck(fullName, citizenId, null).name());
        details.put("pepCheck", PEP_IDS.contains(citizenId) ? "FLAGGED" : "CLEAN");
        details.put("blacklistCheck", BLACKLISTED_IDS.contains(citizenId) ? "BLOCKED" : "CLEAN");
        details.put("sanctionsCheck", "CLEAN");
        details.put("checkedAt", new Date().toString());
        return details;
    }

    private String maskCitizenId(String citizenId) {
        if (citizenId == null || citizenId.length() < 8) return citizenId;
        return citizenId.substring(0, 4) + "xxxx" + citizenId.substring(citizenId.length() - 4);
    }
}
