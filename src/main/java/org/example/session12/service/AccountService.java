package org.example.session12.service;

import org.example.session12.dto.request.AccountRegisterRequest;
import org.example.session12.dto.response.AccountRegisterResponse;

public interface AccountService {
    AccountRegisterResponse registerBasicAccount(AccountRegisterRequest request);
}
