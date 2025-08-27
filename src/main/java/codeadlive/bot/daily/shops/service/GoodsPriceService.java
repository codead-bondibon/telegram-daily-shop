package codeadlive.bot.daily.shops.service;

import codeadlive.bot.daily.shops.entity.GoodsPrice;
import codeadlive.bot.daily.shops.entity.Good;
import codeadlive.bot.daily.shops.entity.Shop;
import codeadlive.bot.daily.shops.repository.GoodsPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoodsPriceService {
    
    private final GoodsPriceRepository goodsPriceRepository;
    private final GoodService goodService;
    private final ShopService shopService;
    
    /**
     * Save a new price
     */
    public GoodsPrice savePrice(GoodsPrice goodsPrice) {
        goodsPrice.setUpdatedAt(LocalDateTime.now());
        return goodsPriceRepository.save(goodsPrice);
    }
    
    /**
     * Create or update price for good in shop
     */
    public GoodsPrice setPrice(String goodId, String shopId, BigDecimal price, String currency) {
        Optional<GoodsPrice> existingPrice = goodsPriceRepository.findByGoodIdAndShopId(goodId, shopId);
        
        if (existingPrice.isPresent()) {
            GoodsPrice priceToUpdate = existingPrice.get();
            priceToUpdate.setPrice(price);
            priceToUpdate.setCurrency(currency);
            priceToUpdate.setUpdatedAt(LocalDateTime.now());
            return goodsPriceRepository.save(priceToUpdate);
        } else {
            Optional<Good> good = goodService.findById(goodId);
            Optional<Shop> shop = shopService.findById(shopId);
            
            if (good.isPresent() && shop.isPresent()) {
                GoodsPrice newPrice = new GoodsPrice(good.get(), shop.get(), price, currency);
                return goodsPriceRepository.save(newPrice);
            } else {
                throw new RuntimeException("Good or Shop not found");
            }
        }
    }
    
    /**
     * Find price by ID
     */
    public Optional<GoodsPrice> findById(String id) {
        return goodsPriceRepository.findById(id);
    }
    
    /**
     * Find price by good and shop
     */
    public Optional<GoodsPrice> findByGoodIdAndShopId(String goodId, String shopId) {
        return goodsPriceRepository.findByGoodIdAndShopId(goodId, shopId);
    }
    
    /**
     * Find all prices for a specific good
     */
    public List<GoodsPrice> findByGoodId(String goodId) {
        return goodsPriceRepository.findByGoodId(goodId);
    }
    
    /**
     * Find all prices for a specific shop
     */
    public List<GoodsPrice> findByShopId(String shopId) {
        return goodsPriceRepository.findByShopId(shopId);
    }
    
    /**
     * Find all prices
     */
    public List<GoodsPrice> findAll() {
        return goodsPriceRepository.findAll();
    }
    
    /**
     * Find prices by good name
     */
    public List<GoodsPrice> findByGoodNameContaining(String goodName) {
        return goodsPriceRepository.findByGoodNameContainingIgnoreCase(goodName);
    }
    
    /**
     * Find prices by shop name
     */
    public List<GoodsPrice> findByShopNameContaining(String shopName) {
        return goodsPriceRepository.findByShopNameContainingIgnoreCase(shopName);
    }
    
    /**
     * Find prices within a price range
     */
    public List<GoodsPrice> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return goodsPriceRepository.findByPriceBetween(minPrice, maxPrice);
    }
    
    /**
     * Find prices by currency
     */
    public List<GoodsPrice> findByCurrency(String currency) {
        return goodsPriceRepository.findByCurrency(currency);
    }
    
    /**
     * Find the cheapest price for a specific good
     */
    public Optional<GoodsPrice> findCheapestPriceForGood(String goodId) {
        List<GoodsPrice> prices = goodsPriceRepository.findByGoodIdOrderByPriceAsc(goodId);
        return prices.isEmpty() ? Optional.empty() : Optional.of(prices.get(0));
    }
    
    /**
     * Find the most expensive price for a specific good
     */
    public Optional<GoodsPrice> findMostExpensivePriceForGood(String goodId) {
        List<GoodsPrice> prices = goodsPriceRepository.findByGoodIdOrderByPriceDesc(goodId);
        return prices.isEmpty() ? Optional.empty() : Optional.of(prices.get(0));
    }
    
    /**
     * Delete price by ID
     */
    public void deleteById(String id) {
        goodsPriceRepository.deleteById(id);
    }
    
    /**
     * Delete price by good and shop
     */
    public void deleteByGoodIdAndShopId(String goodId, String shopId) {
        goodsPriceRepository.findByGoodIdAndShopId(goodId, shopId)
                .ifPresent(goodsPriceRepository::delete);
    }
    
    /**
     * Check if price exists for good and shop
     */
    public boolean existsByGoodIdAndShopId(String goodId, String shopId) {
        return goodsPriceRepository.existsByGoodIdAndShopId(goodId, shopId);
    }
}
