package com.proyecto.unigigs.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyProfileResponse {

    private UUID id;
    private String companyName;
    private String industry;
    private String description;
    private String website;

    // User info
    private UUID userId;
    private String email;
    private String firstName;
    private String lastName;
}
