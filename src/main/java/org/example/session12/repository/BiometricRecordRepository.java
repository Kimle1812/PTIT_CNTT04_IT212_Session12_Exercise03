package org.example.session12.repository;

import org.example.session12.entity.BiometricRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BiometricRecordRepository extends JpaRepository<BiometricRecord, Long> {
    Optional<BiometricRecord> findBySessionId(Long sessionId);
}
