package codeadlive.bot.daily.shops.service;

import codeadlive.bot.daily.shops.entity.Receipt;
import codeadlive.bot.daily.shops.repository.ReceiptRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReceiptService {
    
    private final ReceiptRepository receiptRepository;
    private final OcrService ocrService;
    
    /**
     * Обработка и сохранение чека
     */
    public Receipt processAndSaveReceipt(MultipartFile file) throws IOException, TesseractException {
        log.info("Processing receipt: {}", file.getOriginalFilename());
        
        // OCR обработка
        String originalText = ocrService.processReceiptImage(file);
        String processedText = ocrService.cleanText(originalText);
        
        // Создание объекта чека
        Receipt receipt = new Receipt(originalText, processedText, file.getOriginalFilename());
        
        // Сохранение в БД
        Receipt savedReceipt = receiptRepository.save(receipt);
        
        log.info("Receipt saved with ID: {}", savedReceipt.getId());
        return savedReceipt;
    }
    
    /**
     * Получение чека по ID
     */
    public Optional<Receipt> findById(String id) {
        return receiptRepository.findById(id);
    }
    
    /**
     * Получение всех чеков
     */
    public List<Receipt> findAll() {
        return receiptRepository.findAll();
    }
    
    /**
     * Поиск чеков по тексту
     */
    public List<Receipt> findByTextContaining(String text) {
        return receiptRepository.findByTextContainingIgnoreCase(text);
    }
    
    /**
     * Поиск чеков по имени файла
     */
    public List<Receipt> findByFileNameContaining(String fileName) {
        return receiptRepository.findByFileNameContainingIgnoreCase(fileName);
    }
    
    /**
     * Получение чеков после определенной даты
     */
    public List<Receipt> findByCreatedAtAfter(LocalDateTime date) {
        return receiptRepository.findByCreatedAtAfter(date);
    }
    
    /**
     * Получение чеков в диапазоне дат
     */
    public List<Receipt> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return receiptRepository.findByCreatedAtBetween(startDate, endDate);
    }
    
    /**
     * Удаление чека по ID
     */
    public void deleteById(String id) {
        receiptRepository.deleteById(id);
        log.info("Receipt deleted with ID: {}", id);
    }
    
    /**
     * Обновление чека
     */
    public Receipt updateReceipt(String id, String processedText) {
        return receiptRepository.findById(id)
                .map(receipt -> {
                    receipt.setProcessedText(processedText);
                    receipt.setUpdatedAt(LocalDateTime.now());
                    return receiptRepository.save(receipt);
                })
                .orElseThrow(() -> new RuntimeException("Receipt not found with ID: " + id));
    }
}
