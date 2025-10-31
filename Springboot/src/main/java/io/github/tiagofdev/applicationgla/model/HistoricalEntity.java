package io.github.tiagofdev.applicationgla.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@jakarta.persistence.Entity
@Table(name = "historical_entity", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "date"}) })
public class HistoricalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Getter
    @Setter
    @Column(nullable = false)
    private String name;
    @Getter
    @Setter
    private BigDecimal volumeUsd24Hr;
    @Getter
    @Setter
    private BigDecimal supply;
    @Getter
    @Setter
    private BigDecimal closePrice;
    @Getter
    @Setter
    private BigDecimal openPrice;
    @Getter
    @Setter
    private BigDecimal highPrice;
    @Getter
    @Setter
    private BigDecimal lowPrice;
    @Getter
    @Setter
    @Column(nullable = false)
    private LocalDate date;
}