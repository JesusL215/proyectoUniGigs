package com.proyecto.unigigs.dto.internship;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternshipResponse {

    private UUID id;
    private String title;
    private String description;
    private List<String> requiredSkills;
    private Integer duration;
    private String location;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Company info
    private UUID companyId;
    private String companyName;
    private String industry;
}
