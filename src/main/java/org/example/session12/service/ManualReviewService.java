package org.example.session12.service;

import org.example.session12.entity.EkycSession;
import java.util.List;

public interface ManualReviewService {
    List<EkycSession> getPendingReviewQueue();
    EkycSession getSessionDetail(String sessionId);
    void approveSession(String sessionId, String reviewerId);
    void rejectSession(String sessionId, String reviewerId, String reason);
}
