package org.example.session12.service.impl;

import org.example.session12.service.OcrService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class OcrServiceImpl implements OcrService {

    @Override
    public Map<String, Object> extractDocumentInfo(MultipartFile frontImage, MultipartFile backImage) {
        Map<String, Object> result = new HashMap<>();

        double qualityScore = checkImageSharpness(frontImage);
        if (qualityScore < 0.5) {
            result.put("success", false);
            result.put("error", "Ảnh bị mờ, vui lòng chụp lại ở nơi có ánh sáng tốt");
            result.put("errorCode", "BLUR_DETECTED");
            return result;
        }

        result.put("success", true);
        result.put("documentNumber", "001095012345");
        result.put("fullName", "NGUYEN VAN A");
        result.put("dateOfBirth", "15/05/1990");
        result.put("gender", "NAM");
        result.put("nationality", "Viet Nam");
        result.put("permanentAddress", "Số 123, Đường Láng, Quận Đống Đa, Thành phố Hà Nội");
        result.put("issueDate", "10/01/2020");
        result.put("expiryDate", "10/01/2030");
        result.put("issuePlace", "Cục CSQLHC về TTXH");
        result.put("documentType", "CCCD_CHIP");
        result.put("qualityScore", qualityScore);

        return result;
    }

    @Override
    public Map<String, Object> checkImageQuality(MultipartFile image) {
        Map<String, Object> result = new HashMap<>();

        if (image.isEmpty()) {
            result.put("pass", false);
            result.put("error", "Không có ảnh để kiểm tra");
            return result;
        }

        double blurScore = checkImageSharpness(image);
        boolean hasGlare = checkGlare(image);
        boolean hasCorners = checkCorners(image);

        result.put("pass", blurScore > 0.5 && !hasGlare && hasCorners);
        result.put("blurScore", blurScore);
        result.put("blurDetected", blurScore < 0.5);
        result.put("glareDetected", hasGlare);
        result.put("cornersIntact", hasCorners);

        if (blurScore < 0.5) {
            result.put("error", "Ảnh bị nhòe, xin hãy giữ chắc tay và chụp lại");
        } else if (hasGlare) {
            result.put("error", "Ảnh bị lóa sáng, vui lòng chụp lại ở nơi có ánh sáng vừa phải");
        } else if (!hasCorners) {
            result.put("error", "Ảnh bị mất góc, vui lòng đặt thẻ CCCD vào đúng khung hình");
        }

        return result;
    }

    @Override
    public Map<String, Object> detectForgery(MultipartFile frontImage, MultipartFile backImage) {
        Map<String, Object> result = new HashMap<>();

        double moireScore = detectMoirePattern(frontImage);
        double documentScore = new Random().nextDouble() * 0.3 + 0.7;

        result.put("riskScore", 1.0 - documentScore);
        result.put("moirePatternDetected", moireScore > 0.6);
        result.put("forgeryRiskScore", 1.0 - documentScore);
        result.put("authentic", documentScore > 0.6);
        result.put("message", documentScore > 0.6 ? "Giấy tỏ hợp lệ" : "Phát hiện dấu hiệu bất thường trên giấy tờ");

        return result;
    }

    private double checkImageSharpness(MultipartFile image) {
        return 0.75 + new Random().nextDouble() * 0.2;
    }

    private boolean checkGlare(MultipartFile image) {
        return false;
    }

    private boolean checkCorners(MultipartFile image) {
        return true;
    }

    private double detectMoirePattern(MultipartFile image) {
        return new Random().nextDouble() * 0.3;
    }
}
