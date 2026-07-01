package org.example.session12.service;

import org.example.session12.enums.AmlResult;

import java.util.Map;

public interface AmlCheckService {
    AmlResult performAmlCheck(String fullName, String citizenId, String phone);
    boolean isBlacklisted(String citizenId);
    Map<String, Object> getAmlCheckDetails(String fullName, String citizenId);
}
