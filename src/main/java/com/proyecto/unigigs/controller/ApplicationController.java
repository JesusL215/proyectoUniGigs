package com.proyecto.unigigs.controller;

import com.proyecto.unigigs.dto.application.ApplicationResponse;
import com.proyecto.unigigs.dto.application.CreateApplicationRequest;
import com.proyecto.unigigs.dto.application.UpdateApplicationStatusRequest;
import com.proyecto.unigigs.model.User;
import com.proyecto.unigigs.repository.StudentProfileRepository;
import com.proyecto.unigigs.repository.CompanyProfileRepository;
import com.proyecto.unigigs.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;
    private final StudentProfileRepository studentProfileRepository;
    private final CompanyProfileRepository companyProfileRepository;

    /**
     * Student creates application
     */
    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApplicationResponse> createApplication(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateApplicationRequest request) {
        UUID studentProfileId = studentProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Student profile not found"))
                .getId();

        ApplicationResponse response = applicationService.createApplication(studentProfileId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Company accepts application
     */
    @PutMapping("/{id}/accept")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ApplicationResponse> acceptApplication(
            @PathVariable UUID id,
            @RequestBody UpdateApplicationStatusRequest request) {
        ApplicationResponse response = applicationService.acceptApplication(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Company rejects application
     */
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ApplicationResponse> rejectApplication(
            @PathVariable UUID id,
            @RequestBody UpdateApplicationStatusRequest request) {
        ApplicationResponse response = applicationService.rejectApplication(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Student confirms application (starts internship)
     */
    @PutMapping("/{id}/confirm")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApplicationResponse> confirmApplication(@PathVariable UUID id) {
        ApplicationResponse response = applicationService.confirmApplication(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Student withdraws application
     */
    @PutMapping("/{id}/withdraw")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApplicationResponse> withdrawApplication(@PathVariable UUID id) {
        ApplicationResponse response = applicationService.withdrawApplication(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Company marks application as completed
     */
    @PutMapping("/{id}/complete")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ApplicationResponse> completeApplication(
            @PathVariable UUID id,
            @RequestBody UpdateApplicationStatusRequest request) {
        ApplicationResponse response = applicationService.completeApplication(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get applications by student (authenticated student can see their own)
     */
    @GetMapping("/student/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications(
            @AuthenticationPrincipal User user) {
        UUID studentProfileId = studentProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Student profile not found"))
                .getId();

        List<ApplicationResponse> applications = applicationService.getApplicationsByStudent(studentProfileId);
        return ResponseEntity.ok(applications);
    }

    /**
     * Get applications for an internship (company can see applications for their
     * internships)
     */
    @GetMapping("/internship/{internshipId}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsForInternship(
            @PathVariable UUID internshipId) {
        List<ApplicationResponse> applications = applicationService.getApplicationsByInternship(internshipId);
        return ResponseEntity.ok(applications);
    }

    /**
     * Get single application by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> getApplication(@PathVariable UUID id) {
        ApplicationResponse application = applicationService.getApplicationById(id);
        return ResponseEntity.ok(application);
    }
}
