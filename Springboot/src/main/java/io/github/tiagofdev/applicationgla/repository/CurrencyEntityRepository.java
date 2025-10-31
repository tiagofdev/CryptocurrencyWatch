package io.github.tiagofdev.applicationgla.repository;

import io.github.tiagofdev.applicationgla.model.CurrencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyEntityRepository extends JpaRepository<CurrencyEntity, Long> {
    // You can define custom queries here if needed
}
