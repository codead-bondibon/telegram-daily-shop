package codeadlive.bot.daily.shops.service;

import codeadlive.bot.daily.shops.entity.Good;
import codeadlive.bot.daily.shops.repository.GoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoodService {
    
    private final GoodRepository goodRepository;
    
    /**
     * Save a new good
     */
    public Good saveGood(Good good) {
        return goodRepository.save(good);
    }
    
    /**
     * Find good by ID
     */
    public Optional<Good> findById(String id) {
        return goodRepository.findById(id);
    }
    
    /**
     * Find good by name
     */
    public Optional<Good> findByName(String name) {
        return goodRepository.findByName(name);
    }
    
    /**
     * Find all goods
     */
    public List<Good> findAll() {
        return goodRepository.findAll();
    }
    
    /**
     * Find goods by name containing the given string
     */
    public List<Good> findByNameContaining(String name) {
        return goodRepository.findByNameContainingIgnoreCase(name);
    }
    
    /**
     * Delete good by ID
     */
    public void deleteById(String id) {
        goodRepository.deleteById(id);
    }
    
    /**
     * Check if good exists by name
     */
    public boolean existsByName(String name) {
        return goodRepository.existsByName(name);
    }
}
