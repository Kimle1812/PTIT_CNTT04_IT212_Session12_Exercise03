package org.example.session12.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface OcrService {
    Map<String, Object> extractDocumentInfo(MultipartFile frontImage, MultipartFile backImage);
    Map<String, Object> checkImageQuality(MultipartFile image);
    Map<String, Object> detectForgery(MultipartFile frontImage, MultipartFile backImage);
}
