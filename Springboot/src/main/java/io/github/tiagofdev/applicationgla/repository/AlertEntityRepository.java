package io.github.tiagofdev.applicationgla.repository;


import io.github.tiagofdev.applicationgla.model.AlertEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertEntityRepository extends JpaRepository<AlertEntity, Long> {
    // You can define custom queries here if needed
    List<AlertEntity> findByCurrency(String currency);

    @Query("SELECT a FROM AlertEntity a WHERE a.userEntity.username = :username")
    List<AlertEntity> findByUserEntityUsername(@Param("username") String username);

}
