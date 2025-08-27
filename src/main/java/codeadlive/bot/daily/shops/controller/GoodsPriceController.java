package codeadlive.bot.daily.shops.controller;

import codeadlive.bot.daily.shops.dto.GoodsPriceRequest;
import codeadlive.bot.daily.shops.entity.GoodsPrice;
import codeadlive.bot.daily.shops.service.GoodsPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/prices")
@RequiredArgsConstructor
public class GoodsPriceController {
    
    private final GoodsPriceService goodsPriceService;
    
    /**
     * Create or update a price
     */
    @PostMapping
    public ResponseEntity<GoodsPrice> setPrice(@Valid @RequestBody GoodsPriceRequest request) {
        try {
            GoodsPrice savedPrice = goodsPriceService.setPrice(
                request.getGoodId(), 
                request.getShopId(), 
                request.getPrice(), 
                request.getCurrency()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPrice);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get all prices
     */
    @GetMapping
    public ResponseEntity<List<GoodsPrice>> getAllPrices() {
        List<GoodsPrice> prices = goodsPriceService.findAll();
        return ResponseEntity.ok(prices);
    }
    
    /**
     * Get price by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<GoodsPrice> getPriceById(@PathVariable String id) {
        return goodsPriceService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get price by good and shop
     */
    @GetMapping("/good/{goodId}/shop/{shopId}")
    public ResponseEntity<GoodsPrice> getPriceByGoodAndShop(@PathVariable String goodId, @PathVariable String shopId) {
        return goodsPriceService.findByGoodIdAndShopId(goodId, shopId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get all prices for a specific good
     */
    @GetMapping("/good/{goodId}")
    public ResponseEntity<List<GoodsPrice>> getPricesByGoodId(@PathVariable String goodId) {
        List<GoodsPrice> prices = goodsPriceService.findByGoodId(goodId);
        return ResponseEntity.ok(prices);
    }
    
    /**
     * Get all prices for a specific shop
     */
    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<GoodsPrice>> getPricesByShopId(@PathVariable String shopId) {
        List<GoodsPrice> prices = goodsPriceService.findByShopId(shopId);
        return ResponseEntity.ok(prices);
    }
    
    /**
     * Search prices by good name
     */
    @GetMapping("/search/good")
    public ResponseEntity<List<GoodsPrice>> searchPricesByGoodName(@RequestParam String goodName) {
        List<GoodsPrice> prices = goodsPriceService.findByGoodNameContaining(goodName);
        return ResponseEntity.ok(prices);
    }
    
    /**
     * Search prices by shop name
     */
    @GetMapping("/search/shop")
    public ResponseEntity<List<GoodsPrice>> searchPricesByShopName(@RequestParam String shopName) {
        List<GoodsPrice> prices = goodsPriceService.findByShopNameContaining(shopName);
        return ResponseEntity.ok(prices);
    }
    
    /**
     * Find prices within a price range
     */
    @GetMapping("/range")
    public ResponseEntity<List<GoodsPrice>> getPricesByRange(
            @RequestParam BigDecimal minPrice, 
            @RequestParam BigDecimal maxPrice) {
        List<GoodsPrice> prices = goodsPriceService.findByPriceBetween(minPrice, maxPrice);
        return ResponseEntity.ok(prices);
    }
    
    /**
     * Find prices by currency
     */
    @GetMapping("/currency/{currency}")
    public ResponseEntity<List<GoodsPrice>> getPricesByCurrency(@PathVariable String currency) {
        List<GoodsPrice> prices = goodsPriceService.findByCurrency(currency);
        return ResponseEntity.ok(prices);
    }
    
    /**
     * Get cheapest price for a specific good
     */
    @GetMapping("/good/{goodId}/cheapest")
    public ResponseEntity<GoodsPrice> getCheapestPriceForGood(@PathVariable String goodId) {
        return goodsPriceService.findCheapestPriceForGood(goodId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get most expensive price for a specific good
     */
    @GetMapping("/good/{goodId}/most-expensive")
    public ResponseEntity<GoodsPrice> getMostExpensivePriceForGood(@PathVariable String goodId) {
        return goodsPriceService.findMostExpensivePriceForGood(goodId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Update price
     */
    @PutMapping("/{id}")
    public ResponseEntity<GoodsPrice> updatePrice(@PathVariable String id, @Valid @RequestBody GoodsPriceRequest request) {
        return goodsPriceService.findById(id)
                .map(price -> {
                    GoodsPrice updatedPrice = goodsPriceService.setPrice(
                        request.getGoodId(), 
                        request.getShopId(), 
                        request.getPrice(), 
                        request.getCurrency()
                    );
                    return ResponseEntity.ok(updatedPrice);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Delete price by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrice(@PathVariable String id) {
        if (goodsPriceService.findById(id).isPresent()) {
            goodsPriceService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * Delete price by good and shop
     */
    @DeleteMapping("/good/{goodId}/shop/{shopId}")
    public ResponseEntity<Void> deletePriceByGoodAndShop(@PathVariable String goodId, @PathVariable String shopId) {
        if (goodsPriceService.existsByGoodIdAndShopId(goodId, shopId)) {
            goodsPriceService.deleteByGoodIdAndShopId(goodId, shopId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
