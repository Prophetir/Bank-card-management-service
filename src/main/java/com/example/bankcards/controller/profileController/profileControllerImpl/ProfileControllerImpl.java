package com.example.bankcards.controller.profileController.profileControllerImpl;

import com.example.bankcards.controller.profileController.ProfileController;
import com.example.bankcards.model.dto.profile.CreateProfileFormDto;
import com.example.bankcards.model.dto.profile.ProfileDto;
import com.example.bankcards.model.dto.response.TransactionResponse;
import com.example.bankcards.service.profileService.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileControllerImpl implements ProfileController {

    private final ProfileService profileService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public TransactionResponse createProfile(CreateProfileFormDto profileFormDto) {
        return profileService.createProfile(profileFormDto);
    }

    @GetMapping("/get")
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ProfileDto getProfile(UUID id) {
        return profileService.getProfile(id);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public TransactionResponse deleteProfile(UUID profileId) {
        return profileService.deleteProfile(profileId);
    }

    @PostMapping("/soft-delete")
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public TransactionResponse softDeleteProfile(UUID profileId) {
        return profileService.softDeleteProfile(profileId);
    }
}
