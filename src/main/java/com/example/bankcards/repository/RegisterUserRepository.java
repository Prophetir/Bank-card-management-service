package com.example.bankcards.repository;

import com.example.bankcards.model.entity.RegisterUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RegisterUserRepository extends JpaRepository<RegisterUserEntity, UUID> {

    @Query(value = "SELECT * FROM users WHERE email = :email AND password = :password", nativeQuery = true)
    Optional<RegisterUserEntity> findByEmailAndPassword(@Param("email") String email, @Param("password") String password);
}
