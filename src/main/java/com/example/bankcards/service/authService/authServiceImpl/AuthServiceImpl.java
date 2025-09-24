package com.example.bankcards.service.authService.authServiceImpl;

import com.example.bankcards.exception.exceptions.AlreadyExistsException;
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

import java.rmi.AlreadyBoundException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final RegisterUserRepository userRepository;

    private final JwtTokenService jwtTokenService;

    private final PasswordEncoder passwordEncoder;

    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public JwtTokenDto register(RegisterFormUserDto registerFormDto) {

        if (userRepository.existsByEmail(registerFormDto.getEmail()))
            throw new AlreadyExistsException("User with this email already exists");

        RegisterUserEntity userEntity = modelMapper.map(registerFormDto, RegisterUserEntity.class);

        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));

        userEntity.setUserRole(UserRole.USER);

        userRepository.save(userEntity);

        return jwtTokenService.generateToken(modelMapper.map(userEntity, RegisterUserDto.class));
    }

    @Override
    public JwtTokenDto login(LoginRegisterUserDto loginRegisterUserDto) {

       RegisterUserEntity userEntity = userRepository.findByEmail(loginRegisterUserDto.getEmail())
               .orElseThrow(() -> new NotFoundException("User with this mail not found"));

        if (!passwordEncoder.matches(loginRegisterUserDto.getPassword(), userEntity.getPassword()))
            throw new BadCredentialsException("Wrong password");


        return jwtTokenService.generateToken(modelMapper.map(userEntity, RegisterUserDto.class));
    }
}
