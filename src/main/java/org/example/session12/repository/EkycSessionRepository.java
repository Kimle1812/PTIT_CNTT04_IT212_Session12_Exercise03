package org.example.session12.repository;

import org.example.session12.entity.EkycSession;
import org.example.session12.enums.EkycStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EkycSessionRepository extends JpaRepository<EkycSession, Long> {
    Optional<EkycSession> findBySessionId(String sessionId);
    List<EkycSession> findByStatus(EkycStatus status);
    List<EkycSession> findByPhone(String phone);
    long countByPhoneAndStatus(String phone, EkycStatus status);
}
