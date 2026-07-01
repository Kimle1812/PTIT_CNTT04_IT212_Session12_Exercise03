package org.example.session12.controller;

import lombok.RequiredArgsConstructor;
import org.example.session12.entity.EkycSession;
import org.example.session12.service.ManualReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ekyc/manual-review")
@RequiredArgsConstructor
public class ManualReviewController {

    private final ManualReviewService manualReviewService;

    @GetMapping("/queue")
    public ResponseEntity<List<EkycSession>> getPendingQueue() {
        return ResponseEntity.ok(manualReviewService.getPendingReviewQueue());
    }

    @GetMapping("/detail/{sessionId}")
    public ResponseEntity<EkycSession> getSessionDetail(@PathVariable String sessionId) {
        return ResponseEntity.ok(manualReviewService.getSessionDetail(sessionId));
    }

    @PostMapping("/{sessionId}/approve")
    public ResponseEntity<Map<String, Object>> approveSession(
            @PathVariable String sessionId,
            @RequestParam("reviewerId") String reviewerId) {
        manualReviewService.approveSession(sessionId, reviewerId);
        return ResponseEntity.ok(Map.of(
                "sessionId", sessionId,
                "status", "APPROVED",
                "message", "Hồ sơ đã được phê duyệt thành công."
        ));
    }

    @PostMapping("/{sessionId}/reject")
    public ResponseEntity<Map<String, Object>> rejectSession(
            @PathVariable String sessionId,
            @RequestParam("reviewerId") String reviewerId,
            @RequestParam("reason") String reason) {
        manualReviewService.rejectSession(sessionId, reviewerId, reason);
        return ResponseEntity.ok(Map.of(
                "sessionId", sessionId,
                "status", "REJECTED",
                "message", "Hồ sơ đã bị từ chối."
        ));
    }
}
