package com.example.bankcards.service.authService.authServiceImpl;

import com.example.bankcards.model.dto.jwt.JwtTokenDto;
import com.example.bankcards.model.dto.user.LoginUserDto;
import com.example.bankcards.model.dto.user.RegisterUserDto;
import com.example.bankcards.model.dto.user.UserDto;
import com.example.bankcards.model.entity.UserEntity;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.JwtTokenService;
import com.example.bankcards.service.authService.AuthService;
import com.example.bankcards.util.UserRole;
import jakarta.annotation.Resource;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    @Resource
    private UserRepository userRepository;

    @Resource
    private JwtTokenService jwtTokenService;

    @Resource
    private ModelMapper modelMapper;

    @Override
    public JwtTokenDto register(RegisterUserDto registerDto) {

        UserEntity userEntity = modelMapper.map(registerDto, UserEntity.class);

        userEntity.setUserRole(UserRole.USER);

        System.out.println("---------------------------------------\n" +
                "|name: " + userEntity.getName() + "| \n"
                + "|email: " + userEntity.getEmail() + "| \n"
                + "|password: " + userEntity.getPassword() + "| \n"
                + "|role: " + userEntity.getUserRole() + "| \n"
                + "---------------------------------------\n");

        userRepository.save(userEntity);

        return jwtTokenService.generateToken(modelMapper.map(userEntity, UserDto.class));
    }


    public JwtTokenDto login(LoginUserDto loginUserDto) {

        Optional<UserEntity> userEntity = userRepository.findByEmailAndPassword(
                loginUserDto.getEmail(), loginUserDto.getPassword());

        System.out.println(
                "\n-------| UserEntity for Login |-------\n"
                + "        | Name: " +     userEntity.get().getName() +     " |\n"
                + "        | Email: " +    userEntity.get().getEmail() +    " |\n"
                + "        | Password: " + userEntity.get().getPassword() + " |\n"
                + "        | Role: " +     userEntity.get().getUserRole() +     " |"
                + "\n-------| UserEntity for Login |-------\n"
        );

        if (userEntity.isPresent()) {

            return jwtTokenService.generateToken(modelMapper.map(userEntity.get(), UserDto.class));
        } else throw new RuntimeException();
    }
}
