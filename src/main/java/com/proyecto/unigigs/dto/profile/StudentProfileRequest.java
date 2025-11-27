package com.proyecto.unigigs.dto.profile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileRequest {

    @NotBlank(message = "University is required")
    private String university;

    @NotBlank(message = "Career is required")
    private String career;

    @NotNull(message = "Semester is required")
    private Integer semester;

    private List<String> skills;
    private List<String> interests;
}
