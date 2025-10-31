package io.github.tiagofdev.applicationgla.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@jakarta.persistence.Entity
public class AlertEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;
    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;
    @Getter
    @Setter
    private String currency;
    @Getter
    @Setter
    private boolean alertCriteria; // 1 for Threshold 0 for Percentage
    @Getter
    @Setter
    private double threshold;
    @Getter
    @Setter
    private boolean operator;
    @Getter
    @Setter
    private boolean alertedToday;


    public AlertEntity(UserEntity userEntity, String currency, boolean alertCriteria, boolean operator, double threshold) {

        this.userEntity = userEntity;
        this.currency = currency;
        this.alertCriteria = alertCriteria;
        this.operator = operator;
        this.threshold = threshold;
        this.alertedToday = false;
    }

    public AlertEntity() {

    }
}
