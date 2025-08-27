package codeadlive.bot.daily.shops.repository;

import codeadlive.bot.daily.shops.entity.Receipt;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReceiptRepository extends MongoRepository<Receipt, String> {
    
    /**
     * Find receipts by text containing
     */
    @Query("{ '$or': [ { 'originalText': { $regex: ?0, $options: 'i' } }, { 'processedText': { $regex: ?0, $options: 'i' } } ] }")
    List<Receipt> findByTextContainingIgnoreCase(String text);
    
    /**
     * Find receipts by file name
     */
    List<Receipt> findByFileNameContainingIgnoreCase(String fileName);
    
    /**
     * Find receipts created after date
     */
    List<Receipt> findByCreatedAtAfter(LocalDateTime date);
    
    /**
     * Find receipts created between dates
     */
    List<Receipt> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
