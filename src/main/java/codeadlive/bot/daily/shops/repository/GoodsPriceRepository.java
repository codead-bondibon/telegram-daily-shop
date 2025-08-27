package codeadlive.bot.daily.shops.repository;

import codeadlive.bot.daily.shops.entity.GoodsPrice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface GoodsPriceRepository extends MongoRepository<GoodsPrice, String> {
    
    /**
     * Find price by good and shop
     */
    Optional<GoodsPrice> findByGoodIdAndShopId(String goodId, String shopId);
    
    /**
     * Find all prices for a specific good
     */
    List<GoodsPrice> findByGoodId(String goodId);
    
    /**
     * Find all prices for a specific shop
     */
    List<GoodsPrice> findByShopId(String shopId);
    
    /**
     * Find prices by good name
     */
    @Query("{ 'good.name': { $regex: ?0, $options: 'i' } }")
    List<GoodsPrice> findByGoodNameContainingIgnoreCase(String goodName);
    
    /**
     * Find prices by shop name
     */
    @Query("{ 'shop.name': { $regex: ?0, $options: 'i' } }")
    List<GoodsPrice> findByShopNameContainingIgnoreCase(String shopName);
    
    /**
     * Find prices within a price range
     */
    List<GoodsPrice> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * Find prices by currency
     */
    List<GoodsPrice> findByCurrency(String currency);
    
    /**
     * Find the cheapest price for a specific good
     */
    @Query("{ 'good.$id': ?0 }")
    List<GoodsPrice> findByGoodIdOrderByPriceAsc(String goodId);
    
    /**
     * Find the most expensive price for a specific good
     */
    @Query("{ 'good.$id': ?0 }")
    List<GoodsPrice> findByGoodIdOrderByPriceDesc(String goodId);
    
    /**
     * Check if price exists for good and shop
     */
    boolean existsByGoodIdAndShopId(String goodId, String shopId);
}
