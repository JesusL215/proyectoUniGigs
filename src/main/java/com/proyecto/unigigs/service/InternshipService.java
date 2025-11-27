package com.proyecto.unigigs.service;

import com.proyecto.unigigs.dto.internship.CreateInternshipRequest;
import com.proyecto.unigigs.dto.internship.InternshipResponse;
import com.proyecto.unigigs.dto.internship.UpdateInternshipRequest;
import com.proyecto.unigigs.mapper.InternshipMapper;
import com.proyecto.unigigs.model.CompanyProfile;
import com.proyecto.unigigs.model.Internship;
import com.proyecto.unigigs.repository.CompanyProfileRepository;
import com.proyecto.unigigs.repository.InternshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InternshipService {

    private final InternshipRepository internshipRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final InternshipMapper internshipMapper;

    /**
     * Create new internship (COMPANY only)
     */
    @Transactional
    public InternshipResponse createInternship(UUID companyProfileId, CreateInternshipRequest request) {
        CompanyProfile companyProfile = companyProfileRepository.findById(companyProfileId)
                .orElseThrow(() -> new RuntimeException("Company profile not found"));

        Internship internship = internshipMapper.toEntity(request);
        internship.setCompanyProfile(companyProfile);

        Internship saved = internshipRepository.save(internship);
        return internshipMapper.toResponse(saved);
    }

    /**
     * Get all active internships (public)
     */
    public List<InternshipResponse> getAllActiveInternships() {
        return internshipRepository.findByIsActiveTrue()
                .stream()
                .map(internshipMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get internship by ID
     */
    public InternshipResponse getInternshipById(UUID internshipId) {
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new RuntimeException("Internship not found"));
        return internshipMapper.toResponse(internship);
    }

    /**
     * Get internships by company
     */
    public List<InternshipResponse> getInternshipsByCompany(UUID companyProfileId) {
        return internshipRepository.findByCompanyProfileId(companyProfileId)
                .stream()
                .map(internshipMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update internship (COMPANY only, with ownership validation)
     */
    @Transactional
    public InternshipResponse updateInternship(
            UUID internshipId,
            UUID companyProfileId,
            UpdateInternshipRequest request) {
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new RuntimeException("Internship not found"));

        // Ownership validation
        if (!internship.getCompanyProfile().getId().equals(companyProfileId)) {
            throw new RuntimeException("No tienes permiso para modificar esta pasantía");
        }

        internshipMapper.updateEntity(request, internship);
        Internship saved = internshipRepository.save(internship);
        return internshipMapper.toResponse(saved);
    }

    /**
     * Delete internship (COMPANY only, with ownership validation)
     */
    @Transactional
    public void deleteInternship(UUID internshipId, UUID companyProfileId) {
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new RuntimeException("Internship not found"));

        // Ownership validation
        if (!internship.getCompanyProfile().getId().equals(companyProfileId)) {
            throw new RuntimeException("No tienes permiso para eliminar esta pasantía");
        }

        // Soft delete by marking as inactive
        internship.setIsActive(false);
        internshipRepository.save(internship);
    }
}
