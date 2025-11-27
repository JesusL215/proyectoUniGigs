package com.proyecto.unigigs.dto.internship;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInternshipRequest {

    private String title;
    private String description;
    private List<String> requiredSkills;
    private Integer duration;
    private String location;
    private Boolean isActive;
}
