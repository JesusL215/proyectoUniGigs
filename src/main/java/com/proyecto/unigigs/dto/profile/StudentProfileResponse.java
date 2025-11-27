package com.proyecto.unigigs.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileResponse {

    private UUID id;
    private String university;
    private String career;
    private Integer semester;
    private List<String> skills;
    private List<String> interests;

    // User info
    private UUID userId;
    private String email;
    private String firstName;
    private String lastName;
}
