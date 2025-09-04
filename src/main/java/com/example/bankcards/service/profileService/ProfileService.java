package com.example.bankcards.service.profileService;

import com.example.bankcards.model.dto.profile.CreateProfileFormDto;
import com.example.bankcards.model.dto.profile.ProfileDto;
import com.example.bankcards.model.dto.response.TransactionResponse;

import java.util.UUID;

public interface ProfileService {

    ProfileDto getProfile(UUID id);

    TransactionResponse createProfile(CreateProfileFormDto profileFormDto);

    TransactionResponse deleteProfile(UUID id);

    TransactionResponse softDeleteProfile(UUID id);
}
