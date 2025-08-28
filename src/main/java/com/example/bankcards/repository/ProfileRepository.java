package com.example.bankcards.repository;

import com.example.bankcards.model.entity.UserProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<UserProfileEntity, UUID> {
}
