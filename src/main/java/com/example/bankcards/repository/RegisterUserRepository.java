package com.example.bankcards.repository;

import com.example.bankcards.model.entity.RegisterUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RegisterUserRepository extends JpaRepository<RegisterUserEntity, UUID> {

    Optional<RegisterUserEntity> findByEmail(String email);
}
