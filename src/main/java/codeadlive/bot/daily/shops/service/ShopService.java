package codeadlive.bot.daily.shops.service;

import codeadlive.bot.daily.shops.entity.Shop;
import codeadlive.bot.daily.shops.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShopService {
    
    private final ShopRepository shopRepository;
    
    /**
     * Save a new shop
     */
    public Shop saveShop(Shop shop) {
        return shopRepository.save(shop);
    }
    
    /**
     * Find shop by ID
     */
    public Optional<Shop> findById(String id) {
        return shopRepository.findById(id);
    }
    
    /**
     * Find shop by name
     */
    public Optional<Shop> findByName(String name) {
        return shopRepository.findByName(name);
    }
    
    /**
     * Find all shops
     */
    public List<Shop> findAll() {
        return shopRepository.findAll();
    }
    
    /**
     * Find shops by name containing the given string
     */
    public List<Shop> findByNameContaining(String name) {
        return shopRepository.findByNameContainingIgnoreCase(name);
    }
    
    /**
     * Delete shop by ID
     */
    public void deleteById(String id) {
        shopRepository.deleteById(id);
    }
    
    /**
     * Check if shop exists by name
     */
    public boolean existsByName(String name) {
        return shopRepository.existsByName(name);
    }
}
