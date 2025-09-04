package com.example.bankcards.service.authService.authServiceImpl;

import com.example.bankcards.model.dto.jwt.JwtTokenDto;
import com.example.bankcards.model.dto.user.LoginUserDto;
import com.example.bankcards.model.dto.user.RegisterFormUserDto;
import com.example.bankcards.model.dto.user.RegisterUserDto;
import com.example.bankcards.model.entity.RegisterUserEntity;
import com.example.bankcards.repository.RegisterUserRepository;
import com.example.bankcards.security.JwtTokenService;
import com.example.bankcards.service.authService.AuthService;
import com.example.bankcards.util.UserRole;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final RegisterUserRepository registerUserRepository;

    private final JwtTokenService jwtTokenService;

    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public JwtTokenDto register(RegisterFormUserDto registerDto) {

        RegisterUserEntity registerUserEntity = modelMapper.map(registerDto, RegisterUserEntity.class);

        registerUserEntity.setUserRole(UserRole.USER);

        System.out.println("---------------------------------------\n" +
                "|name: " + registerUserEntity.getName() + "| \n"
                + "|email: " + registerUserEntity.getEmail() + "| \n"
                + "|password: " + registerUserEntity.getPassword() + "| \n"
                + "|role: " + registerUserEntity.getUserRole() + "| \n"
                + "---------------------------------------\n");

        registerUserRepository.save(registerUserEntity);

        return jwtTokenService.generateToken(modelMapper.map(registerUserEntity, RegisterUserDto.class));
    }

    @Transactional
    @Override
    public JwtTokenDto login(LoginUserDto loginUserDto) {

        Optional<RegisterUserEntity> userEntity = registerUserRepository.findByEmailAndPassword(
                loginUserDto.getEmail(), loginUserDto.getPassword());

        System.out.println(
                "\n-------| RegisterUserEntity for Login |-------\n"
                + "        | Name: " +     userEntity.get().getName() +     " |\n"
                + "        | Email: " +    userEntity.get().getEmail() +    " |\n"
                + "        | Password: " + userEntity.get().getPassword() + " |\n"
                + "        | Role: " +     userEntity.get().getUserRole() +     " |"
                + "\n-------| RegisterUserEntity for Login |-------\n"
        );

        if (userEntity.isPresent()) {

            return jwtTokenService.generateToken(modelMapper.map(userEntity.get(), RegisterUserDto.class));
        } else throw new RuntimeException();
    }
}
