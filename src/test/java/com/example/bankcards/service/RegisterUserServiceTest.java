package com.example.bankcards.service;

import com.example.bankcards.exception.exceptions.NotFoundException;
import com.example.bankcards.model.dto.registerUser.UpdateRegisterUserDto;
import com.example.bankcards.model.entity.ProfileEntity;
import com.example.bankcards.model.entity.RegisterUserEntity;
import com.example.bankcards.repository.RegisterUserRepository;
import com.example.bankcards.service.registerUserService.registerUserServiceImpl.RegisterUserServiceImpl;
import com.example.bankcards.util.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
        RegisterUserServiceImpl.class,
        ModelMapper.class
})
public class RegisterUserServiceTest {

    @MockitoBean
    private RegisterUserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RegisterUserServiceImpl userService;

    private RegisterUserEntity userEntity;

    @BeforeEach
    void init() {
        ProfileEntity profileEntity = ProfileEntity.builder()
                .id(UUID.randomUUID())
                .fullName("Siniy Eger").build();

        userEntity = RegisterUserEntity.builder()
                .id(UUID.randomUUID())
                .name("Siniy Anonim")
                .email("shwarsneger@gmail.com")
                .phone("+7 923 (123) 65-21")
                .password("1234567890")
                .userRole(UserRole.USER)
                .userProfile(profileEntity).build();
    }

    @Test
    @DisplayName("Проверяет корректность возвращаемого RegisterUserDto и вызов репозитория")
    void getRegisterUserTest() {
        when(userRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(userEntity));

        userService.getRegisterUser(userEntity.getId());

        verify(userRepository, times(1))
                .findById(any(UUID.class));
    }

    @Test
    @DisplayName("Проверяет корректность выбрасываемого исключения, если пользователь не был найден")
    void getRegisterUser_whileUserNotFoundExceptionTest() {
        when(userRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                userService.getRegisterUser(userEntity.getId()));

        verify(userRepository, times(1))
                .findById(any(UUID.class));
    }

    /**
     * Добавить проверку на валидацию
     * **/
    @Test
    @DisplayName("Проверяет корректность обновления состояния пользователя")
    void updateRegisterUserTest() {
        UpdateRegisterUserDto updateUserData = UpdateRegisterUserDto.builder()
                .email("updated@mail.ru")
                .name("New UpdatedName")
                .password("0987654321")
                .phone("New phone - +7 123 (456) 78-90")
                .build();

        when(userRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(RegisterUserEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<RegisterUserEntity> argumentCaptor = ArgumentCaptor.forClass(RegisterUserEntity.class);

        String transactionResponse = userService.updateRegisterUser(userEntity.getId(), updateUserData).response();

        verify(userRepository, times(1))
                .save(argumentCaptor.capture());

        RegisterUserEntity captorUserEntity = argumentCaptor.getValue();

        verify(userRepository, times(1))
                .findById(any(UUID.class));

        assertThat(captorUserEntity)
                .as("Проверяет корректность установленных значений")
                .extracting(RegisterUserEntity::getId, RegisterUserEntity::getUserRole, RegisterUserEntity::getName,
                        RegisterUserEntity::getPhone, RegisterUserEntity::getEmail, RegisterUserEntity::getPassword,
                        RegisterUserEntity::getUserProfile)
                .containsExactly(userEntity.getId(), userEntity.getUserRole(), updateUserData.getName(),
                        updateUserData.getPhone(), updateUserData.getEmail(), updateUserData.getPassword(),
                        userEntity.getUserProfile());
        assertThat(transactionResponse)
                .as("Проверяет корректность ответного сообщения при успешном завершении обновления")
                .isEqualTo("User by name: %s with rights: %s was successfully updated user"
                        .formatted(captorUserEntity.getName(), captorUserEntity.getUserRole()));
    }

    @Test
    @DisplayName("Проверяет корректность выбрасываемого исключения, если пользователь не был найден")
    void updateRegisterUser_whenUserNotFound_throwNotFoundExceptionTest() {
        when(userRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                userService.updateRegisterUser(userEntity.getId(), UpdateRegisterUserDto.builder().build()));

        verify(userRepository, times(1))
                .findById(any(UUID.class));
    }

    @Test
    @DisplayName("Проверяет корректность soft-delete")
    void softDeleteUser_returnTransactionResponseTest() {
        when(userRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(RegisterUserEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<RegisterUserEntity> argumentCaptor = ArgumentCaptor.forClass(RegisterUserEntity.class);

        String transactionResponse = userService.softDeleteRegisterUser(userEntity.getId()).response();

        verify(userRepository, times(1))
                .save(argumentCaptor.capture());

        RegisterUserEntity captorUser = argumentCaptor.getValue();

        assertThat(captorUser.isSoftDelete())
                .as("Проверяет корректность установки значения soft-delete с false на true")
                .isEqualTo(true);
        assertThat(captorUser)
                .extracting(RegisterUserEntity::getId, RegisterUserEntity::getName, RegisterUserEntity::getPhone,
                        RegisterUserEntity::getEmail, RegisterUserEntity::getPassword, RegisterUserEntity::getUserRole,
                        RegisterUserEntity::getUserProfile, RegisterUserEntity::isSoftDelete)
                .containsExactly(userEntity.getId(), userEntity.getName(), userEntity.getPhone(), userEntity.getEmail(),
                        userEntity.getPassword(), userEntity.getUserRole(), userEntity.getUserProfile(), userEntity.isSoftDelete());
        assertThat(transactionResponse)
                .as("Проверяет корректность ответного сообщения при успешном завершении метода")
                .isEqualTo("User by name: %s was soft-deleted".formatted(userEntity.getName()));
    }

    @Test
    @DisplayName("Проверяет корректность выбрасываемого исключения, если пользователь не был найден")
    void softDeleteUser_whenUserNotFound_throwNotFoundExceptionTest() {
        when(userRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                userService.softDeleteRegisterUser(userEntity.getId()));

        verify(userRepository, times(1))
                .findById(any(UUID.class));
    }

    @Test
    @DisplayName("Проверяет корректность глубокого удаления пользователя")
    void deleteRegisterUserTest() {
        when(userRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(userEntity));

        String transactionResponse = userService.deleteRegisterUser(userEntity.getId()).response();

        verify(userRepository, times(1))
                .findById(any(UUID.class));
        verify(userRepository, times(1))
                .delete(any(RegisterUserEntity.class));

        assertThat(transactionResponse)
                .as("Проверяет корректность возвращаемого сообщения при успешном выполнении метода")
                .isEqualTo("User by name: %s was deleted".formatted(userEntity.getName()));
    }

    @Test
    @DisplayName("Проверяет корректность выбрасываемого исключения, если пользователь не был найден")
    void deleteRegisterUser_whenUserNotFound_throwNotFoundExceptionTest() {
        when(userRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                userService.deleteRegisterUser(userEntity.getId()));

        verify(userRepository, times(1))
                .findById(any(UUID.class));
    }
}
