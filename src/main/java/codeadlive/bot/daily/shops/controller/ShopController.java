package codeadlive.bot.daily.shops.controller;

import codeadlive.bot.daily.shops.dto.ShopRequest;
import codeadlive.bot.daily.shops.entity.Shop;
import codeadlive.bot.daily.shops.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/shops")
@RequiredArgsConstructor
public class ShopController {
    
    private final ShopService shopService;
    
    /**
     * Create a new shop
     */
    @PostMapping
    public ResponseEntity<Shop> createShop(@Valid @RequestBody ShopRequest request) {
        Shop shop = new Shop();
        shop.setName(request.getName());
        Shop savedShop = shopService.saveShop(shop);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedShop);
    }
    
    /**
     * Get all shops
     */
    @GetMapping
    public ResponseEntity<List<Shop>> getAllShops() {
        List<Shop> shops = shopService.findAll();
        return ResponseEntity.ok(shops);
    }
    
    /**
     * Get shop by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Shop> getShopById(@PathVariable String id) {
        return shopService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Update shop by ID
     */
    @PutMapping("/{id}")
    public ResponseEntity<Shop> updateShop(@PathVariable String id, @Valid @RequestBody ShopRequest request) {
        return shopService.findById(id)
                .map(shop -> {
                    shop.setName(request.getName());
                    Shop updatedShop = shopService.saveShop(shop);
                    return ResponseEntity.ok(updatedShop);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Delete shop by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShop(@PathVariable String id) {
        if (shopService.findById(id).isPresent()) {
            shopService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * Find shops by name containing
     */
    @GetMapping("/search")
    public ResponseEntity<List<Shop>> searchShops(@RequestParam String name) {
        List<Shop> shops = shopService.findByNameContaining(name);
        return ResponseEntity.ok(shops);
    }
}
