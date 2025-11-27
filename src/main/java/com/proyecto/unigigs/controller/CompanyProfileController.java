package com.proyecto.unigigs.controller;

import com.proyecto.unigigs.dto.profile.CompanyProfileRequest;
import com.proyecto.unigigs.dto.profile.CompanyProfileResponse;
import com.proyecto.unigigs.model.User;
import com.proyecto.unigigs.service.CompanyProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/company-profiles")
@RequiredArgsConstructor
public class CompanyProfileController {

    private final CompanyProfileService companyProfileService;

    /**
     * Get authenticated company's profile
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<CompanyProfileResponse> getMyProfile(
            @AuthenticationPrincipal User user) {
        CompanyProfileResponse profile = companyProfileService.getProfileByUserId(user.getId());
        return ResponseEntity.ok(profile);
    }

    /**
     * Get company profile by ID (public)
     */
    @GetMapping("/{id}")
    public ResponseEntity<CompanyProfileResponse> getProfile(@PathVariable UUID id) {
        CompanyProfileResponse profile = companyProfileService.getProfileById(id);
        return ResponseEntity.ok(profile);
    }

    /**
     * Update authenticated company's profile
     */
    @PutMapping("/me")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<CompanyProfileResponse> updateMyProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CompanyProfileRequest request) {
        CompanyProfileResponse profile = companyProfileService.updateProfile(user.getId(), request);
        return ResponseEntity.ok(profile);
    }
}
