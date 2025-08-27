package codeadlive.bot.daily.shops.controller;

import codeadlive.bot.daily.shops.service.OcrService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {
    
    private final OcrService ocrService;
    
    /**
     * Тест доступности OCR
     */
    @GetMapping("/ocr")
    public ResponseEntity<Map<String, Object>> testOcr() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean isAvailable = ocrService.isTesseractAvailable();
            response.put("available", isAvailable);
            response.put("status", isAvailable ? "OK" : "NOT_AVAILABLE");
            
            if (isAvailable) {
                // Создаем тестовое изображение
                BufferedImage testImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
                String result = ocrService.processReceiptImage(null); // Это вызовет ошибку, но проверит инициализацию
                response.put("testResult", "OCR initialized successfully");
            } else {
                response.put("error", "Tesseract is not available");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("OCR test failed", e);
            response.put("available", false);
            response.put("status", "ERROR");
            response.put("error", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * Информация о системе
     */
    @GetMapping("/system")
    public ResponseEntity<Map<String, Object>> getSystemInfo() {
        Map<String, Object> info = new HashMap<>();
        
        info.put("javaVersion", System.getProperty("java.version"));
        info.put("osName", System.getProperty("os.name"));
        info.put("osVersion", System.getProperty("os.version"));
        info.put("userDir", System.getProperty("user.dir"));
        info.put("tessdataPath", System.getProperty("tessdata.path"));
        
        return ResponseEntity.ok(info);
    }
}
