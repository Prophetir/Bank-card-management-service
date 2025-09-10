package com.example.bankcards.service.authService.authServiceImpl;

import com.example.bankcards.exception.exceptions.NotFoundException;
import com.example.bankcards.model.dto.jwt.JwtTokenDto;
import com.example.bankcards.model.dto.registerUser.LoginRegisterUserDto;
import com.example.bankcards.model.dto.registerUser.RegisterFormUserDto;
import com.example.bankcards.model.dto.registerUser.RegisterUserDto;
import com.example.bankcards.model.entity.RegisterUserEntity;
import com.example.bankcards.repository.RegisterUserRepository;
import com.example.bankcards.security.JwtTokenService;
import com.example.bankcards.service.authService.AuthService;
import com.example.bankcards.util.UserRole;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final RegisterUserRepository registerUserRepository;

    private final JwtTokenService jwtTokenService;

    private final PasswordEncoder passwordEncoder;

    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public JwtTokenDto register(RegisterFormUserDto registerDto) {

        RegisterUserEntity registerUserEntity = modelMapper.map(registerDto, RegisterUserEntity.class);

        registerUserEntity.setPassword(passwordEncoder.encode(registerUserEntity.getPassword()));

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
    public JwtTokenDto login(LoginRegisterUserDto loginRegisterUserDto) {

       RegisterUserEntity userEntity = registerUserRepository.findByEmail(loginRegisterUserDto.getEmail())
               .orElseThrow(() -> new NotFoundException("User not found"));

        if (!passwordEncoder.matches(loginRegisterUserDto.getPassword(), userEntity.getPassword()))
            throw new BadCredentialsException("Wrong password");

        System.out.println(
                "\n-------| RegisterUserEntity for Login |-------\n"
                + "        | Name: " +     userEntity.getName() +     " |\n"
                + "        | Email: " +    userEntity.getEmail() +    " |\n"
                + "        | Password: " + userEntity.getPassword() + " |\n"
                + "        | Role: " +     userEntity.getUserRole() +     " |"
                + "\n-------| RegisterUserEntity for Login |-------\n"
        );

        return jwtTokenService.generateToken(modelMapper.map(userEntity, RegisterUserDto.class));
    }
}
