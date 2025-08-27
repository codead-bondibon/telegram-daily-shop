package codeadlive.bot.daily.shops.repository;

import codeadlive.bot.daily.shops.entity.Good;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GoodRepository extends MongoRepository<Good, String> {
    
    /**
     * Find good by name
     */
    Optional<Good> findByName(String name);
    
    /**
     * Find all goods by name containing the given string (case-insensitive)
     */
    List<Good> findByNameContainingIgnoreCase(String name);
    
    /**
     * Check if good exists by name
     */
    boolean existsByName(String name);
}
