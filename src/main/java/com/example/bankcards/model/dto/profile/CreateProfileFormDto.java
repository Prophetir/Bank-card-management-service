package com.example.bankcards.model.dto.profile;

import com.example.bankcards.util.DocumentType;
import com.example.bankcards.util.GenderType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateProfileFormDto {

    private String fullName;

    private GenderType gender;

    private String birthday;

    private String phoneNumber;

    private DocumentType documentType;

    private String passportNumber;

    private String passportSeries;

    private String issueBy;

    private String address;

    private String inn;

    private String taxId;

    private String citizenship;

    private String employmentInfo;
}
