package codeadlive.bot.daily.shops.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "goods_prices")
public class GoodsPrice {
    
    @Id
    private String id;
    
    @DBRef
    private Good good;
    
    @DBRef
    private Shop shop;
    
    private BigDecimal price;
    
    private String currency = "USD";
    
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    /**
     * Constructor with required fields
     */
    public GoodsPrice(Good good, Shop shop, BigDecimal price) {
        this.good = good;
        this.shop = shop;
        this.price = price;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Constructor with currency
     */
    public GoodsPrice(Good good, Shop shop, BigDecimal price, String currency) {
        this.good = good;
        this.shop = shop;
        this.price = price;
        this.currency = currency;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
