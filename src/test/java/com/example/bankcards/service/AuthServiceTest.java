package com.example.bankcards.service;

import com.example.bankcards.exception.exceptions.AlreadyExistsException;
import com.example.bankcards.exception.exceptions.NotFoundException;
import com.example.bankcards.model.dto.jwt.JwtTokenDto;
import com.example.bankcards.model.dto.registerUser.LoginRegisterUserDto;
import com.example.bankcards.model.dto.registerUser.RegisterFormUserDto;
import com.example.bankcards.model.dto.registerUser.RegisterUserDto;
import com.example.bankcards.model.entity.ProfileEntity;
import com.example.bankcards.model.entity.RegisterUserEntity;
import com.example.bankcards.repository.RegisterUserRepository;
import com.example.bankcards.security.JwtTokenService;
import com.example.bankcards.service.authService.authServiceImpl.AuthServiceImpl;
import com.example.bankcards.util.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
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
        AuthServiceImpl.class,
        ModelMapper.class
})
public class AuthServiceTest {

    @MockitoBean
    private RegisterUserRepository userRepository;

    @MockitoBean
    private JwtTokenService tokenService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private ModelMapper modelMapper;

    private RegisterUserEntity userEntity;

    @BeforeEach
    public void init() {
        ProfileEntity profileEntity = ProfileEntity.builder().build();

        userEntity = RegisterUserEntity.builder()
                .id(UUID.randomUUID())
                .phone("+7 923 (321) 67-12")
                .email("emailAnonim@mail.ru")
                .name("Anonim Shwarsneger")
                .userProfile(profileEntity)
                .password(("1234567890"))
                .build();
    }

    /**
     * Добавить исключение при некорректной валидации
     * **/
    @Test
    @DisplayName("Проверяет корректность регистрации пользователя")
    void register_returnJwtTokenDtoTest() {
        RegisterFormUserDto registerFormUserDto = createRegisterFormUserDto();

        when(passwordEncoder.encode(anyString()))
                .thenReturn(userEntity.getPassword());
        when(userRepository.save(any(RegisterUserEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(tokenService.generateToken(any(RegisterUserDto.class)))
                .thenReturn(new JwtTokenDto("Null"));

        authService.register(registerFormUserDto);

        ArgumentCaptor<RegisterUserEntity> argumentCaptor = ArgumentCaptor.forClass(RegisterUserEntity.class);

        verify(userRepository, times(1)).save(argumentCaptor.capture());

        RegisterUserEntity captorUserEntity = argumentCaptor.getValue();

        verify(userRepository, times(1))
                .existsByEmail(anyString());
        verify(userRepository, times(1))
                .save(any(RegisterUserEntity.class));
        verify(passwordEncoder, times(1))
                .encode(anyString());
        verify(tokenService, times(1))
                .generateToken(any(RegisterUserDto.class));

        assertThat(captorUserEntity.getUserRole())
                .as("Проверяет роль зарегистрированного пользователя")
                .isEqualTo(UserRole.USER);
    }

    @Test
    @DisplayName("Проверяет корректность выбрасываемого исключения, если email уже записан в БД")
    void register_whenEmailAlreadyExists_throwAlreadyExistsException() {
        RegisterFormUserDto registerFormUserDto = createRegisterFormUserDto();

        when(userRepository.existsByEmail(any(String.class)))
                .thenReturn(true);

        assertThrows(AlreadyExistsException.class, () ->
                authService.register(registerFormUserDto));

        verify(userRepository, times(1))
                .existsByEmail(any(String.class));
    }

    @Test
    @DisplayName("Проверяет аутентификацию пользователя")
    void loginTest_returnJwtTokenDto() {
        when(userRepository.findByEmail(any(String.class)))
                .thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(any(String.class), any(String.class)))
                .thenReturn(true);

        authService.login(new LoginRegisterUserDto(userEntity.getEmail(), userEntity.getPassword()));

        verify(userRepository, times(1))
                .findByEmail(any(String.class));
        verify(passwordEncoder, times(1))
                .matches(any(String.class), any(String.class));
        verify(tokenService, times(1))
                .generateToken(any(RegisterUserDto.class));
    }

    @Test
    @DisplayName("Проверяет корректность выброса исключения NotFoundException, если пользователь не был найден")
    void login_whenNotFountUser_throwNotFoundExceptionTest() {
        when(userRepository.findByEmail(any(String.class)))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> authService.login(
                new LoginRegisterUserDto(userEntity.getEmail(), userEntity.getPassword())));

        verify(userRepository, times(1))
                .findByEmail(any(String.class));
    }

    @Test
    @DisplayName("Проверяет корректность выброса исключения BadCredentialsException, если пароли не совпадают")
    void login_whenWrongPassword_throwBadCredentialsExceptionTest() {
        when(userRepository.findByEmail(any(String.class)))
                .thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(any(String.class), any(String.class)))
                .thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.login(
                new LoginRegisterUserDto(userEntity.getEmail(), "Wrong password")));

        verify(userRepository, times(1))
                .findByEmail(any(String.class));
    }

    private RegisterFormUserDto createRegisterFormUserDto() {
        return RegisterFormUserDto.builder()
                .name(userEntity.getName())
                .phone(userEntity.getPhone())
                .email(userEntity.getEmail())
                .password(userEntity.getPassword())
                .build();
    }
}
