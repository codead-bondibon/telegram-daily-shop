package codeadlive.bot.daily.shops.controller;

import codeadlive.bot.daily.shops.entity.Receipt;
import codeadlive.bot.daily.shops.service.ReceiptService;
import codeadlive.bot.daily.shops.service.OcrService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/receipts")
@RequiredArgsConstructor
public class ReceiptController {
    
    private final ReceiptService receiptService;
    private final OcrService ocrService;
    
    /**
     * Загрузка и обработка чека
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Receipt> uploadReceipt(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            // Проверка типа файла
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                log.error("Invalid content type: {}", contentType);
                return ResponseEntity.badRequest().build();
            }
            
            // Проверка доступности Tesseract
            if (!ocrService.isTesseractAvailable()) {
                log.error("Tesseract OCR is not available");
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(null);
            }
            
            log.info("Processing receipt upload: {} ({} bytes)", 
                    file.getOriginalFilename(), file.getSize());
            
            Receipt receipt = receiptService.processAndSaveReceipt(file);
            return ResponseEntity.status(HttpStatus.CREATED).body(receipt);
            
        } catch (IOException e) {
            log.error("File processing error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (TesseractException e) {
            log.error("OCR processing error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid file format: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Unexpected error during receipt processing", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Проверка статуса OCR сервиса
     */
    @GetMapping("/status")
    public ResponseEntity<String> getOcrStatus() {
        boolean isAvailable = ocrService.isTesseractAvailable();
        if (isAvailable) {
            return ResponseEntity.ok("OCR service is available");
        } else {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("OCR service is not available. Please check Tesseract installation.");
        }
    }
    
    /**
     * Получение всех чеков
     */
    @GetMapping
    public ResponseEntity<List<Receipt>> getAllReceipts() {
        List<Receipt> receipts = receiptService.findAll();
        return ResponseEntity.ok(receipts);
    }
    
    /**
     * Получение чека по ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Receipt> getReceiptById(@PathVariable String id) {
        return receiptService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Поиск чеков по тексту
     */
    @GetMapping("/search")
    public ResponseEntity<List<Receipt>> searchReceipts(@RequestParam String text) {
        List<Receipt> receipts = receiptService.findByTextContaining(text);
        return ResponseEntity.ok(receipts);
    }
    
    /**
     * Поиск чеков по имени файла
     */
    @GetMapping("/search/filename")
    public ResponseEntity<List<Receipt>> searchReceiptsByFileName(@RequestParam String fileName) {
        List<Receipt> receipts = receiptService.findByFileNameContaining(fileName);
        return ResponseEntity.ok(receipts);
    }
    
    /**
     * Получение чеков после определенной даты
     */
    @GetMapping("/after")
    public ResponseEntity<List<Receipt>> getReceiptsAfterDate(@RequestParam String date) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            List<Receipt> receipts = receiptService.findByCreatedAtAfter(dateTime);
            return ResponseEntity.ok(receipts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Получение чеков в диапазоне дат
     */
    @GetMapping("/between")
    public ResponseEntity<List<Receipt>> getReceiptsBetweenDates(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            LocalDateTime end = LocalDateTime.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            List<Receipt> receipts = receiptService.findByCreatedAtBetween(start, end);
            return ResponseEntity.ok(receipts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Обновление обработанного текста чека
     */
    @PutMapping("/{id}")
    public ResponseEntity<Receipt> updateReceipt(@PathVariable String id, @RequestBody String processedText) {
        try {
            Receipt updatedReceipt = receiptService.updateReceipt(id, processedText);
            return ResponseEntity.ok(updatedReceipt);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Удаление чека
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReceipt(@PathVariable String id) {
        if (receiptService.findById(id).isPresent()) {
            receiptService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
