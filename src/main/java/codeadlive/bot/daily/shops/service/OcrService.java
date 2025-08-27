package codeadlive.bot.daily.shops.service;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
public class OcrService {
    
    private final Tesseract tesseract;
    private final String uploadDir = "uploads/receipts/";
    
    public OcrService() {
        this.tesseract = new Tesseract();
        
        // Настройка Tesseract
        try {
            // Путь к данным Tesseract (нужно установить Tesseract на систему)
            // Для Windows: "C:\\Program Files\\Tesseract-OCR\\tessdata"
            // Для Linux: "/usr/share/tessdata"
            // Для Mac: "/usr/local/share/tessdata"
            String tessDataPath = System.getProperty("tessdata.path", "C:/Users/Alexey/Downloads");
            tesseract.setDatapath(tessDataPath);
            
            // Установка языка (русский + английский)
            tesseract.setLanguage("rus+eng");
            
            // Настройки для лучшего распознавания
            tesseract.setPageSegMode(6); // Uniform block of text
            tesseract.setOcrEngineMode(1); // Neural nets LSTM engine
            
            log.info("Tesseract initialized successfully with datapath: {}", tessDataPath);
            
        } catch (Exception e) {
            log.warn("Tesseract configuration failed: {}", e.getMessage());
            log.info("Please install Tesseract OCR and set tessdata.path property");
        }
        
        // Создание директории для загрузок
        createUploadDirectory();
    }
    
    /**
     * Обработка изображения чека
     */
    public String processReceiptImage(MultipartFile file) throws IOException, TesseractException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        log.info("Processing file: {}, size: {} bytes, content type: {}", 
                file.getOriginalFilename(), file.getSize(), file.getContentType());
        
        // Сохранение файла
        String fileName = saveFile(file);
        
        // Чтение и обработка изображения
        BufferedImage originalImage = readImage(file);
        if (originalImage == null) {
            throw new IllegalArgumentException("Invalid image format");
        }
        
        // Предобработка изображения для улучшения OCR
        BufferedImage processedImage = preprocessImage(originalImage);
        
        // Распознавание текста
        String recognizedText = tesseract.doOCR(processedImage);
        
        log.info("OCR completed for file: {}", fileName);
        log.debug("Recognized text length: {} characters", recognizedText.length());
        
        return recognizedText.trim();
    }
    
    /**
     * Чтение изображения с обработкой ошибок
     */
    private BufferedImage readImage(MultipartFile file) throws IOException {
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                log.error("Failed to read image from file: {}", file.getOriginalFilename());
                return null;
            }
            
            log.info("Image read successfully: {}x{} pixels", image.getWidth(), image.getHeight());
            return image;
            
        } catch (IOException e) {
            log.error("Error reading image file: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Предобработка изображения для улучшения OCR
     */
    private BufferedImage preprocessImage(BufferedImage original) {
        int width = original.getWidth();
        int height = original.getHeight();
        
        // Создаем новое изображение с RGB цветовым пространством
        BufferedImage processed = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = processed.createGraphics();
        
        // Настройки рендеринга для лучшего качества
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Рисуем оригинальное изображение
        g2d.drawImage(original, 0, 0, null);
        g2d.dispose();
        
        log.info("Image preprocessed: {}x{} pixels", processed.getWidth(), processed.getHeight());
        return processed;
    }
    
    /**
     * Сохранение файла на диск
     */
    private String saveFile(MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFileName);
        String fileName = UUID.randomUUID().toString() + fileExtension;
        
        Path filePath = Paths.get(uploadDir, fileName);
        Files.copy(file.getInputStream(), filePath);
        
        log.info("File saved: {}", filePath.toString());
        return fileName;
    }
    
    /**
     * Получение расширения файла
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return ".jpg";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
    
    /**
     * Создание директории для загрузок
     */
    private void createUploadDirectory() {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("Created upload directory: {}", uploadPath);
            }
        } catch (IOException e) {
            log.error("Failed to create upload directory", e);
        }
    }
    
    /**
     * Очистка текста от лишних символов
     */
    public String cleanText(String text) {
        if (text == null) return "";
        
        return text
                .replaceAll("[\\r\\n]+", "\n") // Нормализация переносов строк
                .replaceAll("\\s+", " ") // Удаление лишних пробелов
                .trim();
    }
    
    /**
     * Проверка доступности Tesseract
     */
    public boolean isTesseractAvailable() {
        try {
            // Пробуем выполнить простую операцию
            tesseract.doOCR(new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB));
            return true;
        } catch (Exception e) {
            log.warn("Tesseract is not available: {}", e.getMessage());
            return false;
        }
    }
}
