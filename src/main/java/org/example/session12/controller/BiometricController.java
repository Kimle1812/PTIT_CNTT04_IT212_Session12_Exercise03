package org.example.session12.controller;

import lombok.RequiredArgsConstructor;
import org.example.session12.enums.LivenessResult;
import org.example.session12.enums.MatchResult;
import org.example.session12.service.BiometricService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ekyc/biometric")
@RequiredArgsConstructor
public class BiometricController {

    private final BiometricService biometricService;

    @PostMapping("/liveness")
    public ResponseEntity<Map<String, Object>> checkLiveness(
            @RequestParam("sessionId") String sessionId,
            @RequestParam("video") MultipartFile video,
            @RequestParam("challenge") String challenge) throws IOException {

        LivenessResult result = biometricService.performLivenessCheck(video.getBytes(), challenge);

        return ResponseEntity.ok(Map.of(
                "livenessResult", result.name(),
                "passed", LivenessResult.PASS.equals(result),
                "sessionId", sessionId
        ));
    }

    @PostMapping("/face-match")
    public ResponseEntity<Map<String, Object>> faceMatch(
            @RequestParam("sessionId") String sessionId,
            @RequestParam("selfie") MultipartFile selfie,
            @RequestParam("documentPortrait") MultipartFile documentPortrait) throws IOException {

        Map<String, Object> matchResult = biometricService.performFaceMatching(
                selfie.getBytes(), documentPortrait.getBytes());

        matchResult.put("sessionId", sessionId);
        return ResponseEntity.ok(matchResult);
    }
}
