package org.example.session12.repository;

import org.example.session12.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByPhone(String phone);
    boolean existsByEmail(String email);
    boolean existsByCitizenId(String citizenId);
    boolean existsByAccountNumber(String accountNumber);
    Optional<Account> findByPhone(String phone);
    Optional<Account> findByCitizenId(String citizenId);
    Optional<Account> findByAccountId(String accountId);
}
