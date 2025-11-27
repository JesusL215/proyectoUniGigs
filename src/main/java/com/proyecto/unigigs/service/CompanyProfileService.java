package com.proyecto.unigigs.service;

import com.proyecto.unigigs.dto.profile.CompanyProfileRequest;
import com.proyecto.unigigs.dto.profile.CompanyProfileResponse;
import com.proyecto.unigigs.mapper.CompanyProfileMapper;
import com.proyecto.unigigs.model.CompanyProfile;
import com.proyecto.unigigs.repository.CompanyProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyProfileService {

    private final CompanyProfileRepository companyProfileRepository;
    private final CompanyProfileMapper companyProfileMapper;

    /**
     * Get company profile by user ID
     */
    public CompanyProfileResponse getProfileByUserId(UUID userId) {
        CompanyProfile profile = companyProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Company profile not found"));
        return companyProfileMapper.toResponse(profile);
    }

    /**
     * Get company profile by profile ID
     */
    public CompanyProfileResponse getProfileById(UUID profileId) {
        CompanyProfile profile = companyProfileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Company profile not found"));
        return companyProfileMapper.toResponse(profile);
    }

    /**
     * Update company profile
     */
    @Transactional
    public CompanyProfileResponse updateProfile(UUID userId, CompanyProfileRequest request) {
        CompanyProfile profile = companyProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Company profile not found"));

        companyProfileMapper.updateEntity(request, profile);
        CompanyProfile saved = companyProfileRepository.save(profile);
        return companyProfileMapper.toResponse(saved);
    }
}
