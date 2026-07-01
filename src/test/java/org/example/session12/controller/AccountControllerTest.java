package org.example.session12.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.session12.config.SecurityConfig;
import org.example.session12.dto.AccountRegisterRequest;
import org.example.session12.dto.AccountRegisterResponse;
import org.example.session12.service.AccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Lớp Unit Test kiểm thử API Controller đăng ký tài khoản (AccountController).
 * Sử dụng MockMvc để giả lập các cuộc gọi HTTP RESTful, kiểm định dữ liệu validation và mã HTTP status trả về.
 *
 * @author Senior QA Engineer
 * @since 2026-07-01
 */
@WebMvcTest(AccountController.class)
@Import(SecurityConfig.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("TC-01: Gửi Request hợp lệ -> Trả về 201 Created")
    void registerBasicAccount_ValidRequest_ReturnsCreated() throws Exception {
        // GIVEN: Tạo DTO hợp lệ
        AccountRegisterRequest request = AccountRegisterRequest.builder()
                .fullName("Nguyen Van An")
                .phone("0325412365")
                .email("an.nguyen@gmail.com")
                .citizenId("001095012345")
                .build();

        AccountRegisterResponse response = AccountRegisterResponse.builder()
                .accountId("550e8400-e29b-41d4-a716-446655440000")
                .accountNumber("999123456789")
                .status("PENDING")
                .build();

        when(accountService.registerBasicAccount(any(AccountRegisterRequest.class))).thenReturn(response);

        // WHEN & THEN: Thực thi cuộc gọi REST POST và xác thực kết quả
        mockMvc.perform(post("/api/v1/accounts/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountId").value("550e8400-e29b-41d4-a716-446655440000"))
                .andExpect(jsonPath("$.accountNumber").value("999123456789"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("TC-04: Để trống Họ tên -> Trả về 400 Bad Request")
    void registerBasicAccount_EmptyFullName_ReturnsBadRequest() throws Exception {
        // GIVEN: Tên trống
        AccountRegisterRequest request = AccountRegisterRequest.builder()
                .fullName("")
                .phone("0325412365")
                .email("an.nguyen@gmail.com")
                .citizenId("001095012345")
                .build();

        // WHEN & THEN
        mockMvc.perform(post("/api/v1/accounts/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Dữ liệu đầu vào không hợp lệ"))
                .andExpect(jsonPath("$.details.fullName").value("Họ và tên không được để trống"));
    }

    @Test
    @DisplayName("TC-07: Sai định dạng Số điện thoại -> Trả về 400 Bad Request")
    void registerBasicAccount_InvalidPhone_ReturnsBadRequest() throws Exception {
        // GIVEN: SĐT đầu 02 (không phải di động) hoặc không đúng định dạng VN
        AccountRegisterRequest request = AccountRegisterRequest.builder()
                .fullName("Nguyen Van An")
                .phone("0243123456")
                .email("an.nguyen@gmail.com")
                .citizenId("001095012345")
                .build();

        // WHEN & THEN
        mockMvc.perform(post("/api/v1/accounts/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.phone").value("Số điện thoại không đúng định dạng nhà mạng Việt Nam"));
    }

    @Test
    @DisplayName("TC-12: Sai định dạng Email -> Trả về 400 Bad Request")
    void registerBasicAccount_InvalidEmail_ReturnsBadRequest() throws Exception {
        // GIVEN: Email không có @
        AccountRegisterRequest request = AccountRegisterRequest.builder()
                .fullName("Nguyen Van An")
                .phone("0325412365")
                .email("an.nguyen.gmail.com")
                .citizenId("001095012345")
                .build();

        // WHEN & THEN
        mockMvc.perform(post("/api/v1/accounts/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.email").value("Định dạng Email không hợp lệ"));
    }

    @Test
    @DisplayName("TC-18: Số CCCD có mã tỉnh không hợp lệ (000) -> Trả về 400 Bad Request")
    void registerBasicAccount_InvalidCitizenIdProvince_ReturnsBadRequest() throws Exception {
        // GIVEN: CCCD bắt đầu bằng 000 (tỉnh không tồn tại)
        AccountRegisterRequest request = AccountRegisterRequest.builder()
                .fullName("Nguyen Van An")
                .phone("0325412365")
                .email("an.nguyen@gmail.com")
                .citizenId("000095012345")
                .build();

        // WHEN & THEN
        mockMvc.perform(post("/api/v1/accounts/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.citizenId").value("Mã tỉnh/thành phố (3 số đầu) trên CCCD không hợp lệ"));
    }
}
