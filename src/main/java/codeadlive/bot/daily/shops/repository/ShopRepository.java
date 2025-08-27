package codeadlive.bot.daily.shops.repository;

import codeadlive.bot.daily.shops.entity.Shop;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopRepository extends MongoRepository<Shop, String> {
    
    /**
     * Find shop by name
     */
    Optional<Shop> findByName(String name);
    
    /**
     * Find all shops by name containing the given string (case-insensitive)
     */
    List<Shop> findByNameContainingIgnoreCase(String name);
    
    /**
     * Check if shop exists by name
     */
    boolean existsByName(String name);
}
