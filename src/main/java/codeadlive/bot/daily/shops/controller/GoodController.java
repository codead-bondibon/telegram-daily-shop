package codeadlive.bot.daily.shops.controller;

import codeadlive.bot.daily.shops.dto.GoodRequest;
import codeadlive.bot.daily.shops.entity.Good;
import codeadlive.bot.daily.shops.service.GoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/goods")
@RequiredArgsConstructor
public class GoodController {
    
    private final GoodService goodService;
    
    /**
     * Create a new good
     */
    @PostMapping
    public ResponseEntity<Good> createGood(@Valid @RequestBody GoodRequest request) {
        Good good = new Good();
        good.setName(request.getName());
        Good savedGood = goodService.saveGood(good);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedGood);
    }
    
    /**
     * Get all goods
     */
    @GetMapping
    public ResponseEntity<List<Good>> getAllGoods() {
        List<Good> goods = goodService.findAll();
        return ResponseEntity.ok(goods);
    }
    
    /**
     * Get good by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Good> getGoodById(@PathVariable String id) {
        return goodService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Update good by ID
     */
    @PutMapping("/{id}")
    public ResponseEntity<Good> updateGood(@PathVariable String id, @Valid @RequestBody GoodRequest request) {
        return goodService.findById(id)
                .map(good -> {
                    good.setName(request.getName());
                    Good updatedGood = goodService.saveGood(good);
                    return ResponseEntity.ok(updatedGood);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Delete good by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGood(@PathVariable String id) {
        if (goodService.findById(id).isPresent()) {
            goodService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * Find goods by name containing
     */
    @GetMapping("/search")
    public ResponseEntity<List<Good>> searchGoods(@RequestParam String name) {
        List<Good> goods = goodService.findByNameContaining(name);
        return ResponseEntity.ok(goods);
    }
}
