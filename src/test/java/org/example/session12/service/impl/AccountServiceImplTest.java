package org.example.session12.service.impl;

import org.example.session12.dto.AccountRegisterRequest;
import org.example.session12.dto.AccountRegisterResponse;
import org.example.session12.entity.Account;
import org.example.session12.entity.AccountStatus;
import org.example.session12.exception.DuplicateResourceException;
import org.example.session12.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Lớp Unit Test kiểm thử tầng Service xử lý đăng ký tài khoản (AccountServiceImpl).
 * Sử dụng thư viện Mockito để cô lập tầng Service và giả lập kết quả từ Repository.
 *
 * @author Senior QA Engineer
 * @since 2026-07-01
 */
@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    private AccountRegisterRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = AccountRegisterRequest.builder()
                .fullName("Nguyen Van An")
                .phone("0325412365")
                .email("an.nguyen@gmail.com")
                .citizenId("001095012345")
                .build();
    }

    @Test
    @DisplayName("TC-01: Đăng ký tài khoản hợp lệ - Thành công")
    void registerBasicAccount_ValidRequest_Success() {
        // GIVEN: Giả lập các điều kiện kiểm tra trùng lặp đều không tồn tại
        when(accountRepository.existsByPhone(validRequest.getPhone())).thenReturn(false);
        when(accountRepository.existsByEmail(validRequest.getEmail())).thenReturn(false);
        when(accountRepository.existsByCitizenId(validRequest.getCitizenId())).thenReturn(false);
        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(false);

        // Giả lập lưu thành công trả về thực thể Account có ID và UUID
        Account mockSavedAccount = Account.builder()
                .id(1L)
                .accountId("550e8400-e29b-41d4-a716-446655440000")
                .fullName(validRequest.getFullName())
                .phone(validRequest.getPhone())
                .email(validRequest.getEmail())
                .citizenId(validRequest.getCitizenId())
                .accountNumber("999123456789")
                .status(AccountStatus.PENDING)
                .build();
        when(accountRepository.save(any(Account.class))).thenReturn(mockSavedAccount);

        // WHEN: Thực hiện gọi hàm xử lý nghiệp vụ đăng ký
        AccountRegisterResponse response = accountService.registerBasicAccount(validRequest);

        // THEN: Kiểm chứng kết quả trả về khớp với mong đợi
        assertNotNull(response);
        assertEquals("550e8400-e29b-41d4-a716-446655440000", response.getAccountId());
        assertEquals("999123456789", response.getAccountNumber());
        assertEquals("PENDING", response.getStatus());

        // Kiểm tra xem Repository có được gọi chính xác các phương thức cần thiết hay không
        verify(accountRepository, times(1)).existsByPhone(validRequest.getPhone());
        verify(accountRepository, times(1)).existsByEmail(validRequest.getEmail());
        verify(accountRepository, times(1)).existsByCitizenId(validRequest.getCitizenId());
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    @DisplayName("TC-10: Đăng ký trùng Số điện thoại - Trả về lỗi DuplicateResourceException")
    void registerBasicAccount_DuplicatePhone_ThrowsException() {
        // GIVEN: Giả lập số điện thoại đã tồn tại trong database
        when(accountRepository.existsByPhone(validRequest.getPhone())).thenReturn(true);

        // WHEN & THEN: Gọi hàm và kiểm chứng ném ra ngoại lệ DuplicateResourceException
        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> {
            accountService.registerBasicAccount(validRequest);
        });

        assertEquals("Số điện thoại này đã được đăng ký sử dụng.", exception.getMessage());
        
        // Xác nhận không thực hiện các bước lưu hay kiểm tra trùng phía sau
        verify(accountRepository, never()).existsByEmail(anyString());
        verify(accountRepository, never()).existsByCitizenId(anyString());
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("TC-13: Đăng ký trùng Email - Trả về lỗi DuplicateResourceException")
    void registerBasicAccount_DuplicateEmail_ThrowsException() {
        // GIVEN: SĐT không trùng nhưng Email trùng
        when(accountRepository.existsByPhone(validRequest.getPhone())).thenReturn(false);
        when(accountRepository.existsByEmail(validRequest.getEmail())).thenReturn(true);

        // WHEN & THEN
        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> {
            accountService.registerBasicAccount(validRequest);
        });

        assertEquals("Địa chỉ email này đã được đăng ký sử dụng.", exception.getMessage());

        verify(accountRepository, times(1)).existsByPhone(validRequest.getPhone());
        verify(accountRepository, times(1)).existsByEmail(validRequest.getEmail());
        verify(accountRepository, never()).existsByCitizenId(anyString());
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("TC-20: Đăng ký trùng Số CCCD - Trả về lỗi DuplicateResourceException")
    void registerBasicAccount_DuplicateCitizenId_ThrowsException() {
        // GIVEN: SĐT và Email không trùng nhưng CCCD trùng
        when(accountRepository.existsByPhone(validRequest.getPhone())).thenReturn(false);
        when(accountRepository.existsByEmail(validRequest.getEmail())).thenReturn(false);
        when(accountRepository.existsByCitizenId(validRequest.getCitizenId())).thenReturn(true);

        // WHEN & THEN
        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> {
            accountService.registerBasicAccount(validRequest);
        });

        assertEquals("Số CCCD này đã được đăng ký cho một tài khoản khác.", exception.getMessage());

        verify(accountRepository, times(1)).existsByPhone(validRequest.getPhone());
        verify(accountRepository, times(1)).existsByEmail(validRequest.getEmail());
        verify(accountRepository, times(1)).existsByCitizenId(validRequest.getCitizenId());
        verify(accountRepository, never()).save(any(Account.class));
    }
}
