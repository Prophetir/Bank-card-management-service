package com.example.bankcards.service.registerUserService.registerUserServiceImpl;

import com.example.bankcards.exception.exceptions.NotFoundException;
import com.example.bankcards.model.dto.registerUser.RegisterUserDto;
import com.example.bankcards.model.dto.registerUser.UpdateRegisterUserDto;
import com.example.bankcards.model.dto.response.TransactionResponse;
import com.example.bankcards.model.entity.RegisterUserEntity;
import com.example.bankcards.repository.RegisterUserRepository;
import com.example.bankcards.service.registerUserService.RegisterUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterUserServiceImpl implements RegisterUserService {

    private final RegisterUserRepository userRepository;

    private final ModelMapper modelMapper;

    @Override
    public RegisterUserDto getRegisterUser(UUID id) {
        return modelMapper.map(
                userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found")),
                RegisterUserDto.class);
    }

    @Transactional
    @Override
    public TransactionResponse updateRegisterUser(UUID id, UpdateRegisterUserDto updateUserData) {
        RegisterUserEntity updatedRegUserEntity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        modelMapper.map(updateUserData, updatedRegUserEntity);

        userRepository.save(updatedRegUserEntity);

        return new TransactionResponse("User by name: %s with rights: %s was successfully updated user"
                .formatted(updatedRegUserEntity.getName(), updatedRegUserEntity.getUserRole()));
    }

    @Transactional
    @Override
    public TransactionResponse deleteRegisterUser(UUID id) {
        RegisterUserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with this id not found"));

        String userName = userEntity.getName();

        userRepository.delete(userEntity);

        return new TransactionResponse("User by name: %s was deleted".formatted(userName));
    }

    @Transactional
    @Override
    public TransactionResponse softDeleteRegisterUser(UUID id) {
        RegisterUserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with this id not found"));

        userEntity.setSoftDelete(true);

        userRepository.save(userEntity);

        return new TransactionResponse("User by name: %s was soft-deleted".formatted(userEntity.getName()));
    }
}
