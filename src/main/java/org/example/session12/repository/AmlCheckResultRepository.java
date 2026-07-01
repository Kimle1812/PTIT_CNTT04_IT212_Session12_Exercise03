package org.example.session12.repository;

import org.example.session12.entity.AmlCheckResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AmlCheckResultRepository extends JpaRepository<AmlCheckResult, Long> {
    Optional<AmlCheckResult> findBySessionId(Long sessionId);
}
