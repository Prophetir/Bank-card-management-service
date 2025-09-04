package com.example.bankcards.service.userService.serviceImpl;

import com.example.bankcards.exception.exceptions.NotFoundException;
import com.example.bankcards.model.dto.response.TransactionResponse;
import com.example.bankcards.model.dto.user.LoginUserDto;
import com.example.bankcards.model.dto.user.RegisterUserDto;
import com.example.bankcards.model.entity.RegisterUserEntity;
import com.example.bankcards.repository.RegisterUserRepository;
import com.example.bankcards.service.userService.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final RegisterUserRepository regUserRepository;

    private final ModelMapper modelMapper;

    @Override
    public RegisterUserDto getUser(UUID id) {
        return modelMapper.map(regUserRepository.findById(id), RegisterUserDto.class);
    }

    @Override
    public TransactionResponse deleteUser(UUID id) {
        RegisterUserEntity user = regUserRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found by id"));

        String name = user.getName();

        regUserRepository.delete(user);

        return new TransactionResponse("User by name: %s was deleted".formatted(name));
    }

    @Override
    public TransactionResponse softDeleteUser(UUID id) {
        RegisterUserEntity user = regUserRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found by id"));

        user.setSoftDelete(true);

        return new TransactionResponse("User by name: %s was soft-deleted".formatted(user.getName()));
    }
}
