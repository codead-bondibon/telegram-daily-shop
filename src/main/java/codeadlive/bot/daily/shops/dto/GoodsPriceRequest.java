package codeadlive.bot.daily.shops.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

@Data
public class GoodsPriceRequest {
    
    @NotBlank(message = "Good ID is required")
    private String goodId;
    
    @NotBlank(message = "Shop ID is required")
    private String shopId;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;
    
    private String currency = "USD";
}
