package com.proyecto.unigigs.dto.internship;

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
public class CreateInternshipRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private List<String> requiredSkills;

    @NotNull(message = "Duration is required")
    private Integer duration;

    @NotBlank(message = "Location is required")
    private String location;
}
