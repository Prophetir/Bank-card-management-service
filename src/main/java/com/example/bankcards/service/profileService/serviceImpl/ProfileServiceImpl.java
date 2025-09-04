package com.example.bankcards.service.profileService.serviceImpl;

import com.example.bankcards.exception.exceptions.NotFoundException;
import com.example.bankcards.exception.exceptions.NotFoundProfileForCardCreateException;
import com.example.bankcards.model.dto.card.CreateCardFormDto;
import com.example.bankcards.model.dto.profile.CreateProfileFormDto;
import com.example.bankcards.model.dto.profile.ProfileDto;
import com.example.bankcards.model.dto.response.TransactionResponse;
import com.example.bankcards.model.entity.ProfileEntity;
import com.example.bankcards.repository.ProfileRepository;
import com.example.bankcards.service.profileService.ProfileDomainService;
import com.example.bankcards.service.profileService.ProfileService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService, ProfileDomainService {

    private final ProfileRepository profileRepository;

    private final ModelMapper modelMapper;

    @Override
    public ProfileDto getProfile(UUID id) {
        return modelMapper.map(profileRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Profile not found")),
                ProfileDto.class);
    }

    /**
     * В случае, если профиль не будет найден, следовало бы отправлять простое сообщение,
     * в котором говориться, что профиль не существует и необходимо создать новый профиль,
     * а не выбрасывать исключение.
     * Однако этого пока достаточно.
     **/
    @Override
    public ProfileEntity findProfileByPassportData(CreateCardFormDto createFormDto) {
        return profileRepository.findOne(((root, query, criteria) -> criteria.and(
                        criteria.equal(root.get("passportNumber"), createFormDto.getPassportNumber()),
                        criteria.equal(root.get("passportSeries"), createFormDto.getPassportSeries()),
                        criteria.equal(root.get("documentType"), createFormDto.getDocumentType()),
                        criteria.equal(root.get("citizenship"), createFormDto.getCitizenship()),
                        criteria.equal(root.get("address"), createFormDto.getAddress()),
                        criteria.equal(root.get("fullName"), createFormDto.getFullName()),
                        criteria.equal(root.get("gender"), createFormDto.getGender()),
                        criteria.equal(root.get("birthday"), createFormDto.getBirthday()))))
                .orElseThrow(() -> new NotFoundProfileForCardCreateException(
                        "Profile not exist. Required create new profile"));
    }

    @Transactional
    @Override
    public TransactionResponse createProfile(CreateProfileFormDto cardFormDto) {

        ProfileEntity profileEntity = ProfileEntity.builder()
                .fullName(cardFormDto.getFullName())
                .gender(cardFormDto.getGender())
                .birthday(cardFormDto.getBirthday())
                .phoneNumber(cardFormDto.getPhoneNumber())
                .documentType(cardFormDto.getDocumentType())
                .passportNumber(cardFormDto.getPassportNumber())
                .passportSeries(cardFormDto.getPassportSeries())
                .issueBy(cardFormDto.getIssueBy())
                .address(cardFormDto.getAddress())
                .taxId(cardFormDto.getTaxId())
                .citizenship(cardFormDto.getCitizenship())
                .employmentInfo(cardFormDto.getEmploymentInfo())
                .build();

        profileRepository.save(profileEntity);

        return new TransactionResponse("Profile successfully created");
    }

    @Override
    public ProfileEntity getProfileEntity(UUID userId) {
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Profile not found"));
    }

    @Transactional
    @Override
    public TransactionResponse deleteProfile(UUID id) {
        ProfileEntity profile = profileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Profile not found"));

        String name = profile.getFullName();

        profileRepository.delete(profile);

        return new TransactionResponse("Profile by name: %s was deleted".formatted(name));
    }

    @Transactional
    @Override
    public TransactionResponse softDeleteProfile(UUID id) {
        ProfileEntity profile = profileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Profile not found"));

        profile.setSoftDelete(true);

        return new TransactionResponse("Profile by name: %s was soft-deleted".formatted(profile.getFullName()));
    }
}
