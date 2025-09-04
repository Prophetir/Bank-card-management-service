package com.example.bankcards.model.dto.profile;

import com.example.bankcards.util.GenderType;

import java.util.UUID;

public class ProfileDto {

    private UUID id;

    private String email;

    private String password;

    private String fullName;

    private GenderType gender;

    private String birthday;

    private String phoneNumber;

    private boolean softDelete;
}
