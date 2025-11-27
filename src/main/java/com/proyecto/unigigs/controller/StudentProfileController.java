package com.proyecto.unigigs.controller;

import com.proyecto.unigigs.dto.profile.StudentProfileRequest;
import com.proyecto.unigigs.dto.profile.StudentProfileResponse;
import com.proyecto.unigigs.model.User;
import com.proyecto.unigigs.service.StudentProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/student-profiles")
@RequiredArgsConstructor
public class StudentProfileController {

    private final StudentProfileService studentProfileService;

    /**
     * Get authenticated student's profile
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudentProfileResponse> getMyProfile(
            @AuthenticationPrincipal User user) {
        StudentProfileResponse profile = studentProfileService.getProfileByUserId(user.getId());
        return ResponseEntity.ok(profile);
    }

    /**
     * Get student profile by ID (public)
     */
    @GetMapping("/{id}")
    public ResponseEntity<StudentProfileResponse> getProfile(@PathVariable UUID id) {
        StudentProfileResponse profile = studentProfileService.getProfileById(id);
        return ResponseEntity.ok(profile);
    }

    /**
     * Update authenticated student's profile
     */
    @PutMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudentProfileResponse> updateMyProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody StudentProfileRequest request) {
        StudentProfileResponse profile = studentProfileService.updateProfile(user.getId(), request);
        return ResponseEntity.ok(profile);
    }
}
