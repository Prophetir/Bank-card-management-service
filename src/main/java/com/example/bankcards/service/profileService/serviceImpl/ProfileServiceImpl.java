package com.example.bankcards.service.profileService.serviceImpl;

import com.example.bankcards.exception.exceptions.AlreadyExistsException;
import com.example.bankcards.exception.exceptions.NotFoundException;
import com.example.bankcards.exception.exceptions.NotFoundProfileException;
import com.example.bankcards.model.dto.card.PassportData;
import com.example.bankcards.model.dto.profile.CreateProfileFormDto;
import com.example.bankcards.model.dto.profile.ProfileDto;
import com.example.bankcards.model.dto.response.TransactionResponse;
import com.example.bankcards.model.entity.ProfileEntity;
import com.example.bankcards.repository.ProfileRepository;
import com.example.bankcards.service.profileService.ProfileDomainService;
import com.example.bankcards.service.profileService.ProfileService;
import com.example.bankcards.util.MaskPhoneAndCardNumber;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService, ProfileDomainService {

    private final ProfileRepository profileRepository;

    private final ModelMapper modelMapper;

    @Override
    public ProfileDto getProfile(UUID id) {
        ProfileDto profileDto = modelMapper.map(profileRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Profile not found")),
                ProfileDto.class);

        profileDto.setPhoneNumber(MaskPhoneAndCardNumber.maskPhoneNumber(profileDto.getPhoneNumber()));

        return profileDto;
    }

    /**
     * В случае, если профиль не будет найден, следовало бы отправлять простое сообщение,
     * в котором говориться, что профиль не существует и необходимо создать новый профиль,
     * а не выбрасывать исключение.
     * Однако этого пока достаточно.
     **/
    @Override
    public ProfileEntity findProfileByPassportData(PassportData passportData) {
        return profileRepository.findOne(createSpecificationPassportData(passportData))
                .orElseThrow(() ->
                        new NotFoundProfileException("Profile not exist. Required create new profile"));
    }

    @Transactional
    @Override
    public TransactionResponse createProfile(CreateProfileFormDto cardFormDto) {

        if (profileRepository.exists(createSpecificationPassportData(
                modelMapper.map(cardFormDto, PassportData.class))))
            throw new AlreadyExistsException("Profile already exist. Required create new profile");

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

        return new TransactionResponse("Profile by name: $s with related user: $s successfully created"
                .formatted(profileEntity.getFullName(), profileEntity.getRelatedUser()));
    }

    @Override
    public ProfileEntity getProfileByUserId(UUID userId) {
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

    private Specification<ProfileEntity> createSpecificationPassportData(PassportData passportData) {
        return (root, query, criteria) -> criteria.and(
                criteria.equal(root.get("passportNumber"), passportData.getPassportNumber()),
                criteria.equal(root.get("passportSeries"), passportData.getPassportSeries()),
                criteria.equal(root.get("documentType"), passportData.getDocumentType()),
                criteria.equal(root.get("citizenship"), passportData.getCitizenship()),
                criteria.equal(root.get("address"), passportData.getAddress()),
                criteria.equal(root.get("fullName"), passportData.getFullName()),
                criteria.equal(root.get("gender"), passportData.getGender()),
                criteria.equal(root.get("birthday"), passportData.getBirthday()));
    }
}
