package com.example.bankcards.controller.profileController;

import com.example.bankcards.model.dto.profile.CreateProfileFormDto;
import com.example.bankcards.model.dto.profile.ProfileDto;
import com.example.bankcards.model.dto.response.TransactionResponse;

import java.util.UUID;

public interface ProfileController {

    ProfileDto getProfile(UUID profileId);

    TransactionResponse createProfile(CreateProfileFormDto profileFormDto);

    TransactionResponse deleteProfile(UUID profileId);

    TransactionResponse softDeleteProfile(UUID profileId);
}
