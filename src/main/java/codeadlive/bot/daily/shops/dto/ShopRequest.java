package codeadlive.bot.daily.shops.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class ShopRequest {
    
    @NotBlank(message = "Shop name is required")
    private String name;
}
