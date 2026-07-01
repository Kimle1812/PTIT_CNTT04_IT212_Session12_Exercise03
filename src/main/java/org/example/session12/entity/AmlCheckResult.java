package org.example.session12.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.session12.enums.AmlResult;
import java.time.LocalDateTime;

@Entity
@Table(name = "aml_check_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AmlCheckResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private EkycSession session;

    @Enumerated(EnumType.STRING)
    @Column(name = "aml_result", nullable = false, length = 20)
    private AmlResult amlResult;

    @Column(name = "pep_check_result", length = 20)
    private String pepCheckResult;

    @Column(name = "blacklist_check_result", length = 20)
    private String blacklistCheckResult;

    @Column(name = "sanctions_check_result", length = 20)
    private String sanctionsCheckResult;

    @Column(name = "matched_list_name", length = 200)
    private String matchedListName;

    @Column(name = "risk_score")
    private Integer riskScore;

    @Column(name = "details_json", columnDefinition = "TEXT")
    private String detailsJson;

    @Column(name = "checked_at", nullable = false)
    private LocalDateTime checkedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (checkedAt == null) {
            checkedAt = LocalDateTime.now();
        }
    }
}
