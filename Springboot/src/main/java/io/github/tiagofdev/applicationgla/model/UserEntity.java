package io.github.tiagofdev.applicationgla.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@jakarta.persistence.Entity
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;
    @Getter
    @Setter
    private String username;
    @Getter
    @Setter
    private String password;

    public UserEntity() {}

    // Add this constructor
    public UserEntity(String username, String password) {
        this.username = username;
        this.password = password;
    }



}