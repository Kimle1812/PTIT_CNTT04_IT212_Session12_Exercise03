package org.example.session12.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.session12.dto.request.AccountRegisterRequest;
import org.example.session12.dto.response.AccountRegisterResponse;
import org.example.session12.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/register")
    public ResponseEntity<AccountRegisterResponse> registerBasicAccount(
            @Valid @RequestBody AccountRegisterRequest request) {
        AccountRegisterResponse response = accountService.registerBasicAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
