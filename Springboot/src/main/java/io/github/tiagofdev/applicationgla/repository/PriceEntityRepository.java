package io.github.tiagofdev.applicationgla.repository;

import io.github.tiagofdev.applicationgla.model.PriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PriceEntityRepository extends JpaRepository<PriceEntity, Long> {
    // You can define custom queries here if needed
    @Query("SELECT a FROM PriceEntity a WHERE a.collectedAt BETWEEN :startDate AND :endDate AND a.name = :name ORDER BY a.collectedAt")
    List<PriceEntity> findByNameDateRange(@Param("name") String name,
                                      @Param("endDate") LocalDateTime endDate,
                                      @Param("startDate") LocalDateTime startDate);
}
