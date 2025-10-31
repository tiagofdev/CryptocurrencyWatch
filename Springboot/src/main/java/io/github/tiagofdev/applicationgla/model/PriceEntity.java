package io.github.tiagofdev.applicationgla.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@jakarta.persistence.Entity
public class PriceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private BigDecimal priceUsd;
    @Getter
    @Setter
    private LocalDateTime collectedAt;


}

