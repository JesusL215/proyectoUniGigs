package com.proyecto.unigigs.dto.profile;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyProfileRequest {

    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "Industry is required")
    private String industry;

    private String description;
    private String website;
}
