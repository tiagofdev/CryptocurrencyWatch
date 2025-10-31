package io.github.tiagofdev.applicationgla.repository;

import io.github.tiagofdev.applicationgla.model.HistoricalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface HistoricalEntityRepository extends JpaRepository<HistoricalEntity, Long> {
    // You can define custom queries here if needed
    Optional<HistoricalEntity> findByNameAndDate(String name, LocalDate date);
}
