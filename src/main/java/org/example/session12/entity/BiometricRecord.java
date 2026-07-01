package org.example.session12.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "biometric_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BiometricRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private EkycSession session;

    @Column(name = "liveness_video_path", length = 500)
    private String livenessVideoPath;

    @Column(name = "selfie_image_path", length = 500)
    private String selfieImagePath;

    @Column(name = "liveness_pass")
    private Boolean livenessPass;

    @Column(name = "liveness_confidence")
    private Double livenessConfidence;

    @Column(name = "face_match_score")
    private Double faceMatchScore;

    @Column(name = "face_match_result", length = 20)
    private String faceMatchResult;

    @Column(name = "challenge_type", length = 100)
    private String challengeType;

    @Column(name = "matched_face_image_path", length = 500)
    private String matchedFaceImagePath;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
