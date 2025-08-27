package codeadlive.bot.daily.shops.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "receipts")
public class Receipt {
    
    @Id
    private String id;
    
    private String originalText;
    
    private String processedText;
    
    private String imageUrl;
    
    private String fileName;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    /**
     * Constructor with required fields
     */
    public Receipt(String originalText, String processedText, String fileName) {
        this.originalText = originalText;
        this.processedText = processedText;
        this.fileName = fileName;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
