package io.github.tiagofdev.applicationgla.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@jakarta.persistence.Entity
public class CurrencyEntity {

    @Id
    @Getter
    @Setter
    private String id;
    @Getter
    @Setter
    @Column(nullable = false)
    private String name;
    @Getter
    @Setter
    private String symbol;
    @Getter
    @Setter
    private String explorer;
    @Getter
    @Setter
    private String icon;
    @Getter
    @Setter
    private int rank;
}