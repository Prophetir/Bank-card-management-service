package com.example.bankcards.model.dto.profile;

import com.example.bankcards.util.GenderType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Data
@Getter
@Setter
public class ProfileDto {

    private UUID id;

    private String email;

    private String fullName;

    private GenderType gender;

    private String birthday;

    private String phoneNumber;

    private boolean softDelete;
}
