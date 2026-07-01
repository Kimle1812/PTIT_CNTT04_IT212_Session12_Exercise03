package org.example.session12.controller;

import lombok.RequiredArgsConstructor;
import org.example.session12.service.OcrService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/ekyc/document")
@RequiredArgsConstructor
public class DocumentController {

    private final OcrService ocrService;

    @PostMapping("/capture")
    public ResponseEntity<Map<String, Object>> captureDocument(
            @RequestParam("sessionId") String sessionId,
            @RequestParam("frontImage") MultipartFile frontImage,
            @RequestParam("backImage") MultipartFile backImage) {

        Map<String, Object> qualityCheck = ocrService.checkImageQuality(frontImage);
        if (!Boolean.TRUE.equals(qualityCheck.get("pass"))) {
            return ResponseEntity.badRequest().body(qualityCheck);
        }

        Map<String, Object> qualityCheckBack = ocrService.checkImageQuality(backImage);
        if (!Boolean.TRUE.equals(qualityCheckBack.get("pass"))) {
            return ResponseEntity.badRequest().body(qualityCheckBack);
        }

        Map<String, Object> forgeryCheck = ocrService.detectForgery(frontImage, backImage);
        Map<String, Object> ocrResult = ocrService.extractDocumentInfo(frontImage, backImage);

        ocrResult.put("forgeryCheck", forgeryCheck);
        return ResponseEntity.ok(ocrResult);
    }

    @PostMapping("/check-quality")
    public ResponseEntity<Map<String, Object>> checkQuality(@RequestParam("image") MultipartFile image) {
        return ResponseEntity.ok(ocrService.checkImageQuality(image));
    }
}
