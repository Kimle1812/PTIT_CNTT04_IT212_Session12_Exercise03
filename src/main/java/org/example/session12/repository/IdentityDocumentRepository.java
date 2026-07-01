package org.example.session12.repository;

import org.example.session12.entity.IdentityDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface IdentityDocumentRepository extends JpaRepository<IdentityDocument, Long> {
    Optional<IdentityDocument> findBySessionId(Long sessionId);
}
