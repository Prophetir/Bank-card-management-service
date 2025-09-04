package com.example.bankcards.service.profileService;

import com.example.bankcards.model.dto.card.CreateCardFormDto;
import com.example.bankcards.model.entity.ProfileEntity;

import java.util.UUID;

public interface ProfileDomainService  {
    ProfileEntity getProfileEntity(UUID id);

    ProfileEntity findProfileByPassportData(CreateCardFormDto createFormDto);
}
