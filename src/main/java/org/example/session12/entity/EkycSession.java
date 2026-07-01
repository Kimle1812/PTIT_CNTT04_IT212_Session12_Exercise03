package org.example.session12.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.session12.enums.EkycStatus;
import java.time.LocalDateTime;

@Entity
@Table(name = "ekyc_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EkycSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", unique = true, nullable = false, length = 36)
    private String sessionId;

    @Column(name = "phone", nullable = false, length = 15)
    private String phone;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "device_id", length = 100)
    private String deviceId;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private EkycStatus status;

    @Column(name = "otp_sent_at")
    private LocalDateTime otpSentAt;

    @Column(name = "otp_verified_at")
    private LocalDateTime otpVerifiedAt;

    @Column(name = "otp_attempts")
    @Builder.Default
    private Integer otpAttempts = 0;

    @Column(name = "face_scan_attempts")
    @Builder.Default
    private Integer faceScanAttempts = 0;

    @Column(name = "face_scan_locked_until")
    private LocalDateTime faceScanLockedUntil;

    @Column(name = "ocr_score")
    private Double ocrScore;

    @Column(name = "liveness_result", length = 20)
    private String livenessResult;

    @Column(name = "face_match_score")
    private Double faceMatchScore;

    @Column(name = "face_match_result", length = 20)
    private String faceMatchResult;

    @Column(name = "aml_result", length = 20)
    private String amlResult;

    @Column(name = "document_risk_score")
    private Double documentRiskScore;

    @Column(name = "nfc_verified")
    @Builder.Default
    private Boolean nfcVerified = false;

    @Column(name = "manual_review_required")
    @Builder.Default
    private Boolean manualReviewRequired = false;

    @Column(name = "manual_review_reason", length = 500)
    private String manualReviewReason;

    @Column(name = "manual_reviewed_by", length = 50)
    private String manualReviewedBy;

    @Column(name = "manual_reviewed_at")
    private LocalDateTime manualReviewedAt;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @Column(name = "cif", length = 20)
    private String cif;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
