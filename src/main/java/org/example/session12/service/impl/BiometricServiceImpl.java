package org.example.session12.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.session12.enums.LivenessResult;
import org.example.session12.enums.MatchResult;
import org.example.session12.service.BiometricService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class BiometricServiceImpl implements BiometricService {

    private static final double STP_THRESHOLD = 0.85;
    private static final double MANUAL_REVIEW_THRESHOLD = 0.70;
    private static final Random RANDOM = new Random();

    @Override
    public LivenessResult performLivenessCheck(byte[] videoData, String challengeType) {
        double livenessConfidence = 0.85 + RANDOM.nextDouble() * 0.15;

        if (livenessConfidence >= 0.7) {
            return LivenessResult.PASS;
        } else if (livenessConfidence >= 0.4) {
            return LivenessResult.INCONCLUSIVE;
        } else {
            return LivenessResult.FAIL;
        }
    }

    @Override
    public Map<String, Object> performFaceMatching(byte[] selfieImage, byte[] documentPortrait) {
        Map<String, Object> result = new HashMap<>();

        double similarityScore = 0.75 + RANDOM.nextDouble() * 0.20;

        result.put("similarityScore", similarityScore);
        result.put("matchResult", evaluateMatchScore(similarityScore).name());
        result.put("matched", similarityScore >= STP_THRESHOLD);

        return result;
    }

    @Override
    public MatchResult evaluateMatchScore(double similarityScore) {
        if (similarityScore >= STP_THRESHOLD) {
            return MatchResult.APPROVED;
        } else if (similarityScore >= MANUAL_REVIEW_THRESHOLD) {
            return MatchResult.WARNING;
        } else {
            return MatchResult.REJECTED;
        }
    }
}
