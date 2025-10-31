package io.github.tiagofdev.applicationgla.repository;
import io.github.tiagofdev.applicationgla.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {
    // You can define custom queries here if needed
    Optional<UserEntity> findByUsername(String username);
}
