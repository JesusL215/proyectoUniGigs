package com.proyecto.unigigs.dto.application;

import com.proyecto.unigigs.model.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResponse {

    private UUID id;
    private ApplicationStatus status;
    private String message;
    private String feedback;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Student info
    private UUID studentId;
    private String studentName;
    private String studentEmail;
    private String university;
    private String career;

    // Internship info
    private UUID internshipId;
    private String internshipTitle;
    private String companyName;
}
