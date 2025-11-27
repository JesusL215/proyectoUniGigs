package com.proyecto.unigigs.controller;

import com.proyecto.unigigs.dto.internship.CreateInternshipRequest;
import com.proyecto.unigigs.dto.internship.InternshipResponse;
import com.proyecto.unigigs.dto.internship.UpdateInternshipRequest;
import com.proyecto.unigigs.model.User;
import com.proyecto.unigigs.repository.CompanyProfileRepository;
import com.proyecto.unigigs.service.InternshipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/internships")
@RequiredArgsConstructor
public class InternshipController {

    private final InternshipService internshipService;
    private final CompanyProfileRepository companyProfileRepository;

    /**
     * Create new internship (COMPANY only)
     */
    @PostMapping
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<InternshipResponse> createInternship(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateInternshipRequest request) {
        UUID companyProfileId = companyProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Company profile not found"))
                .getId();

        InternshipResponse response = internshipService.createInternship(companyProfileId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all active internships (public)
     */
    @GetMapping
    public ResponseEntity<List<InternshipResponse>> getAllActiveInternships() {
        List<InternshipResponse> internships = internshipService.getAllActiveInternships();
        return ResponseEntity.ok(internships);
    }

    /**
     * Get internship by ID (public)
     */
    @GetMapping("/{id}")
    public ResponseEntity<InternshipResponse> getInternship(@PathVariable UUID id) {
        InternshipResponse internship = internshipService.getInternshipById(id);
        return ResponseEntity.ok(internship);
    }

    /**
     * Get internships by authenticated company
     */
    @GetMapping("/company/me")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<List<InternshipResponse>> getMyInternships(
            @AuthenticationPrincipal User user) {
        UUID companyProfileId = companyProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Company profile not found"))
                .getId();

        List<InternshipResponse> internships = internshipService.getInternshipsByCompany(companyProfileId);
        return ResponseEntity.ok(internships);
    }

    /**
     * Update internship (COMPANY only, ownership validated)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<InternshipResponse> updateInternship(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateInternshipRequest request) {
        UUID companyProfileId = companyProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Company profile not found"))
                .getId();

        InternshipResponse response = internshipService.updateInternship(id, companyProfileId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete internship (COMPANY only, ownership validated)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<Void> deleteInternship(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        UUID companyProfileId = companyProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Company profile not found"))
                .getId();

        internshipService.deleteInternship(id, companyProfileId);
        return ResponseEntity.noContent().build();
    }
}
