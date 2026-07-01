package org.example.session12.service;

import org.example.session12.enums.LivenessResult;
import org.example.session12.enums.MatchResult;

import java.util.Map;

public interface BiometricService {
    LivenessResult performLivenessCheck(byte[] videoData, String challengeType);
    Map<String, Object> performFaceMatching(byte[] selfieImage, byte[] documentPortrait);
    MatchResult evaluateMatchScore(double similarityScore);
}
