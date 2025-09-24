package com.example.bankcards.model.dto.profile;

import com.example.bankcards.util.GenderType;
import lombok.*;

import java.util.UUID;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDto {

    private UUID id;

    private String fullName;

    private GenderType gender;

    private String birthday;

    private String phoneNumber;

    private boolean softDelete;
}
