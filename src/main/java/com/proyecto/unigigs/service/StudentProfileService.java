package com.proyecto.unigigs.service;

import com.proyecto.unigigs.dto.profile.StudentProfileRequest;
import com.proyecto.unigigs.dto.profile.StudentProfileResponse;
import com.proyecto.unigigs.mapper.StudentProfileMapper;
import com.proyecto.unigigs.model.StudentProfile;
import com.proyecto.unigigs.repository.StudentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentProfileService {

    private final StudentProfileRepository studentProfileRepository;
    private final StudentProfileMapper studentProfileMapper;

    /**
     * Get student profile by user ID
     */
    public StudentProfileResponse getProfileByUserId(UUID userId) {
        StudentProfile profile = studentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));
        return studentProfileMapper.toResponse(profile);
    }

    /**
     * Get student profile by profile ID
     */
    public StudentProfileResponse getProfileById(UUID profileId) {
        StudentProfile profile = studentProfileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));
        return studentProfileMapper.toResponse(profile);
    }

    /**
     * Update student profile
     */
    @Transactional
    public StudentProfileResponse updateProfile(UUID userId, StudentProfileRequest request) {
        StudentProfile profile = studentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        studentProfileMapper.updateEntity(request, profile);
        StudentProfile saved = studentProfileRepository.save(profile);
        return studentProfileMapper.toResponse(saved);
    }
}
