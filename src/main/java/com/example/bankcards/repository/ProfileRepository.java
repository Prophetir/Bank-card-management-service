package com.example.bankcards.repository;

import com.example.bankcards.model.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<ProfileEntity, UUID>, JpaSpecificationExecutor<ProfileEntity> {

    @Query("select p from ProfileEntity p where p.relatedUser.id = :userId")
    Optional<ProfileEntity> findByUserId(@Param("userId") UUID userId);
}
