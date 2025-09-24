package com.example.bankcards.service;

import com.example.bankcards.exception.exceptions.AlreadyExistsException;
import com.example.bankcards.exception.exceptions.NotFoundException;
import com.example.bankcards.exception.exceptions.NotFoundProfileException;
import com.example.bankcards.model.dto.card.PassportData;
import com.example.bankcards.model.dto.profile.CreateProfileFormDto;
import com.example.bankcards.model.dto.profile.ProfileDto;
import com.example.bankcards.model.entity.ProfileEntity;
import com.example.bankcards.repository.ProfileRepository;
import com.example.bankcards.service.profileService.serviceImpl.ProfileServiceImpl;
import com.example.bankcards.util.DocumentType;
import com.example.bankcards.util.GenderType;
import com.example.bankcards.util.MaskPhoneAndCardNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        ModelMapper.class,
        ProfileServiceImpl.class
})
public class ProfileServiceTest {

    @MockitoBean
    private ProfileRepository profileRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ProfileServiceImpl profileService;

    private ProfileEntity profileEntity;

    @BeforeEach
    void init() {
        profileEntity = ProfileEntity.builder()
                .id(UUID.randomUUID())
                .fullName("Junet Chaykovsky")
                .birthday("23.05.2000")
                .phoneNumber("+7 923 (567) 76-90")
                .passportNumber("89 56")
                .passportSeries("586412")
                .address("Moscow, 12 street Gagarin's, 56 home, 23 apartments")
                .taxId("111222333444555666777888999000")
                .citizenship("Russian")
                .gender(GenderType.MAN)
                .documentType(DocumentType.PASSPORT)
                .issueBy("Holy Matrona").build();
    }

    @Test
    @DisplayName("Проверяет корректность возвращаемого ProfileEntity и вызов репозитория")
    void getProfileTest() {
        String expectedPhoneNumber = MaskPhoneAndCardNumber.maskPhoneNumber(profileEntity.getPhoneNumber());

        when(profileRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(profileEntity));

        ProfileDto resultProfile = profileService.getProfile(profileEntity.getId());

        verify(profileRepository, times(1))
                .findById(any(UUID.class));

        assertThat(resultProfile.getPhoneNumber())
                .as("Проверяет корректность маскирования номера телефона")
                .isEqualTo(expectedPhoneNumber);

        assertThat(resultProfile)
                .as("Проверяет соответствие полей возвращаемого объекта")
                .extracting(ProfileDto::getId, ProfileDto::getFullName, ProfileDto::getGender,
                        ProfileDto::getBirthday, ProfileDto::getPhoneNumber)
                .containsExactly(profileEntity.getId(), profileEntity.getFullName(), profileEntity.getGender(),
                        profileEntity.getBirthday(), expectedPhoneNumber);
    }

    @Test
    @DisplayName("Проверяет корректность выбрасываемого исключения NotFoundException, если профиль не был найден")
    void getProfile_whileProfileNotFoundExceptionTest() {
        when(profileRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                profileService.getProfile(profileEntity.getId()));

        verify(profileRepository, times(1))
                .findById(any(UUID.class));
    }

    @Test
    @DisplayName("Проверяет корректность возвращаемого значения ProfileEntity при поиске по паспортным данным PassportData")
    void findProfileByPassportData() {
        when(profileRepository.findOne(any(Specification.class)))
                .thenReturn(Optional.of(profileEntity));

        ProfileEntity resultProfile = profileService.findProfileByPassportData(createPassportData());

        verify(profileRepository, times(1))
                .findOne(any(Specification.class));

        assertThat(resultProfile)
                .as("Проверяет корректность возвращаемого объекта")
                .isEqualTo(profileEntity);
    }

    @Test
    @DisplayName("Проверяет корректность выбрасываемого исключения NotFoundProfileException, если профиль не был найден")
    void findProfileByPassportData_whileNotFoundProfileExceptionTest() {
        when(profileRepository.findOne(any(Specification.class)))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundProfileException.class, () ->
                profileService.findProfileByPassportData(createPassportData()));

        verify(profileRepository, times(1))
                .findOne(any(Specification.class));
    }

    @Test
    @DisplayName("Проверяет корректность создания и сохранения нового ProfileEntity")
    void createProfileTest() {

        when(profileRepository.save(any(ProfileEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<ProfileEntity> argumentCaptor = ArgumentCaptor.forClass(ProfileEntity.class);

        String transactionResponse = profileService.createProfile(createProfileFormDto()).response();

        verify(profileRepository, times(1))
                .save(argumentCaptor.capture());

        ProfileEntity savedProfile = argumentCaptor.getValue();

        assertThat(transactionResponse)
                .as("Проверяет корректность ответного сообщение при успешном завершении метода")
                .isEqualTo("Profile by name: $s with related user: $s successfully created"
                        .formatted(savedProfile.getFullName(), savedProfile.getRelatedUser()));

        assertThat(savedProfile)
                .as("Проверяет корректность полей вернувшегося объекта")
                .extracting(ProfileEntity::getFullName, ProfileEntity::getGender, ProfileEntity::getBirthday,
                        ProfileEntity::getPhoneNumber, ProfileEntity::getDocumentType, ProfileEntity::getPassportNumber,
                        ProfileEntity::getPassportSeries, ProfileEntity::getAddress, ProfileEntity::getTaxId,
                        ProfileEntity::getIssueBy, ProfileEntity::getCitizenship, ProfileEntity::getEmploymentInfo,
                        ProfileEntity::getRelatedUser, ProfileEntity::isSoftDelete)
                .containsExactly(profileEntity.getFullName(), profileEntity.getGender(), profileEntity.getBirthday(),
                        profileEntity.getPhoneNumber(), profileEntity.getDocumentType(), profileEntity.getPassportNumber(),
                        profileEntity.getPassportSeries(), profileEntity.getAddress(), profileEntity.getTaxId(),
                        profileEntity.getIssueBy(), profileEntity.getCitizenship(), profileEntity.getEmploymentInfo(),
                        profileEntity.getRelatedUser(), profileEntity.isSoftDelete());
    }

    @Test
    @DisplayName("Проверяет корректность выбрасываемого исключения, если создаваемый профиль уже существует")
    void createProfile_whenProfileAlreadyExists_throwAlreadyExistsExceptionTest() {
        when(profileRepository.exists(any(Specification.class)))
                .thenReturn(true);

        assertThrows(AlreadyExistsException.class, () ->
                profileService.createProfile(createProfileFormDto()));

        verify(profileRepository, times(1))
                .exists(any(Specification.class));
    }

    private PassportData createPassportData() {
        return PassportData.builder()
                .fullName(profileEntity.getFullName())
                .passportNumber(profileEntity.getPassportNumber())
                .passportSeries(profileEntity.getPassportSeries())
                .address(profileEntity.getAddress())
                .gender(profileEntity.getGender().toString())
                .documentType(profileEntity.getDocumentType().toString())
                .birthday(profileEntity.getBirthday())
                .citizenship(profileEntity.getCitizenship())
                .build();
    }

    private CreateProfileFormDto createProfileFormDto() {
        return CreateProfileFormDto.builder()
                .fullName(profileEntity.getFullName())
                .taxId(profileEntity.getTaxId())
                .phoneNumber(profileEntity.getPhoneNumber())
                .passportNumber(profileEntity.getPassportNumber())
                .passportSeries(profileEntity.getPassportSeries())
                .address(profileEntity.getAddress())
                .birthday(profileEntity.getBirthday())
                .gender(profileEntity.getGender())
                .issueBy(profileEntity.getIssueBy())
                .documentType(profileEntity.getDocumentType())
                .citizenship(profileEntity.getCitizenship())
                .build();
    }
}
