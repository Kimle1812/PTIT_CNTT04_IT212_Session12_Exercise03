package org.example.session12.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.session12.enums.DocumentType;
import java.time.LocalDateTime;

@Entity
@Table(name = "identity_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdentityDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private EkycSession session;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 20)
    private DocumentType documentType;

    @Column(name = "document_number", nullable = false, length = 20)
    private String documentNumber;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "date_of_birth")
    private LocalDateTime dateOfBirth;

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "nationality", length = 50)
    private String nationality;

    @Column(name = "permanent_address", length = 500)
    private String permanentAddress;

    @Column(name = "issue_date")
    private LocalDateTime issueDate;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "issue_place", length = 100)
    private String issuePlace;

    @Column(name = "front_image_path", length = 500)
    private String frontImagePath;

    @Column(name = "back_image_path", length = 500)
    private String backImagePath;

    @Column(name = "nfc_data_json", columnDefinition = "TEXT")
    private String nfcDataJson;

    @Column(name = "image_quality_pass")
    @Builder.Default
    private Boolean imageQualityPass = false;

    @Column(name = "blur_detected")
    @Builder.Default
    private Boolean blurDetected = false;

    @Column(name = "glare_detected")
    @Builder.Default
    private Boolean glareDetected = false;

    @Column(name = "forgery_risk_score")
    private Double forgeryRiskScore;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
