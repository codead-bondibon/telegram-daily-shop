package codeadlive.bot.daily.shops.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class GoodRequest {
    
    @NotBlank(message = "Good name is required")
    private String name;
}
