package com.proyecto.unigigs.service;

import com.proyecto.unigigs.dto.application.ApplicationResponse;
import com.proyecto.unigigs.dto.application.CreateApplicationRequest;
import com.proyecto.unigigs.dto.application.UpdateApplicationStatusRequest;
import com.proyecto.unigigs.mapper.ApplicationMapper;
import com.proyecto.unigigs.model.*;
import com.proyecto.unigigs.repository.ApplicationRepository;
import com.proyecto.unigigs.repository.InternshipRepository;
import com.proyecto.unigigs.repository.StudentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final InternshipRepository internshipRepository;
    private final ApplicationMapper applicationMapper;

    /**
     * RULE OF 5: Validates that student doesn't exceed 5 active applications
     */
    private void validateActiveApplicationsLimit(UUID studentProfileId) {
        List<ApplicationStatus> activeStatuses = List.of(
                ApplicationStatus.PENDIENTE,
                ApplicationStatus.SELECCIONADO);

        long activeCount = applicationRepository.countByStudentProfileIdAndStatusIn(
                studentProfileId,
                activeStatuses);

        if (activeCount >= 5) {
            throw new RuntimeException(
                    "Has alcanzado el límite de 5 postulaciones activas. " +
                            "Por favor, espera a que se resuelvan algunas antes de postular a más.");
        }
    }

    /**
     * EXCLUSIVITY CHECK: Validates student doesn't have another internship in
     * progress
     */
    private void validateExclusivity(UUID studentProfileId) {
        boolean hasActiveInternship = applicationRepository.existsByStudentProfileIdAndStatus(
                studentProfileId,
                ApplicationStatus.EN_PROCESO);

        if (hasActiveInternship) {
            throw new RuntimeException(
                    "Ya tienes una pasantía en proceso. " +
                            "No puedes comenzar otra hasta completar la actual.");
        }
    }

    /**
     * Create a new application (Student applies to internship)
     */
    @Transactional
    public ApplicationResponse createApplication(UUID studentProfileId, CreateApplicationRequest request) {
        // Validate Rule of 5
        validateActiveApplicationsLimit(studentProfileId);

        StudentProfile studentProfile = studentProfileRepository.findById(studentProfileId)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        Internship internship = internshipRepository.findById(request.getInternshipId())
                .orElseThrow(() -> new RuntimeException("Internship not found"));

        if (!internship.getIsActive()) {
            throw new RuntimeException("This internship is no longer active");
        }

        // Check if student already applied to this internship
        List<Application> existing = applicationRepository.findByStudentProfileId(studentProfileId);
        boolean alreadyApplied = existing.stream()
                .anyMatch(app -> app.getInternship().getId().equals(request.getInternshipId()) &&
                        app.getStatus() != ApplicationStatus.RECHAZADA &&
                        app.getStatus() != ApplicationStatus.CANCELADA);

        if (alreadyApplied) {
            throw new RuntimeException("Ya has postulado a esta pasantía");
        }

        Application application = Application.builder()
                .studentProfile(studentProfile)
                .internship(internship)
                .status(ApplicationStatus.PENDIENTE)
                .message(request.getMessage())
                .build();

        Application saved = applicationRepository.save(application);
        return applicationMapper.toResponse(saved);
    }

    /**
     * Company accepts application (PENDIENTE -> SELECCIONADO)
     */
    @Transactional
    public ApplicationResponse acceptApplication(UUID applicationId, UpdateApplicationStatusRequest request) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (application.getStatus() != ApplicationStatus.PENDIENTE) {
            throw new RuntimeException("Solo puedes aceptar postulaciones en estado PENDIENTE");
        }

        application.setStatus(ApplicationStatus.SELECCIONADO);
        if (request.getFeedback() != null) {
            application.setFeedback(request.getFeedback());
        }

        Application saved = applicationRepository.save(application);
        return applicationMapper.toResponse(saved);
    }

    /**
     * Company rejects application (PENDIENTE -> RECHAZADA)
     */
    @Transactional
    public ApplicationResponse rejectApplication(UUID applicationId, UpdateApplicationStatusRequest request) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (application.getStatus() != ApplicationStatus.PENDIENTE) {
            throw new RuntimeException("Solo puedes rechazar postulaciones en estado PENDIENTE");
        }

        application.setStatus(ApplicationStatus.RECHAZADA);
        if (request.getFeedback() != null) {
            application.setFeedback(request.getFeedback());
        }

        Application saved = applicationRepository.save(application);
        return applicationMapper.toResponse(saved);
    }

    /**
     * Student confirms and starts internship (SELECCIONADO -> EN_PROCESO)
     * Includes exclusivity check and auto-cancellation of other pending
     * applications
     */
    @Transactional
    public ApplicationResponse confirmApplication(UUID applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (application.getStatus() != ApplicationStatus.SELECCIONADO) {
            throw new RuntimeException("Solo puedes confirmar postulaciones en estado SELECCIONADO");
        }

        UUID studentProfileId = application.getStudentProfile().getId();

        // Validate exclusivity
        validateExclusivity(studentProfileId);

        // Update status to EN_PROCESO
        application.setStatus(ApplicationStatus.EN_PROCESO);
        Application saved = applicationRepository.save(application);

        // Auto-cancel other pending/selected applications
        List<ApplicationStatus> cancelableStatuses = List.of(
                ApplicationStatus.PENDIENTE,
                ApplicationStatus.SELECCIONADO);

        List<Application> otherApplications = applicationRepository
                .findByStudentProfileIdAndStatusIn(studentProfileId, cancelableStatuses)
                .stream()
                .filter(app -> !app.getId().equals(applicationId))
                .collect(Collectors.toList());

        for (Application app : otherApplications) {
            app.setStatus(ApplicationStatus.CANCELADA);
            app.setFeedback("Cancelada automáticamente al confirmar otra pasantía");
        }

        applicationRepository.saveAll(otherApplications);

        return applicationMapper.toResponse(saved);
    }

    /**
     * Student withdraws application (-> CANCELADA)
     * Only allowed if not started yet
     */
    @Transactional
    public ApplicationResponse withdrawApplication(UUID applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (application.getStatus() == ApplicationStatus.EN_PROCESO ||
                application.getStatus() == ApplicationStatus.COMPLETADA) {
            throw new RuntimeException(
                    "No puedes retirarte de una pasantía ya iniciada o completada");
        }

        if (application.getStatus() == ApplicationStatus.RECHAZADA ||
                application.getStatus() == ApplicationStatus.CANCELADA) {
            throw new RuntimeException("Esta postulación ya está cerrada");
        }

        application.setStatus(ApplicationStatus.CANCELADA);
        Application saved = applicationRepository.save(application);
        return applicationMapper.toResponse(saved);
    }

    /**
     * Complete internship (EN_PROCESO -> COMPLETADA)
     * AUTO-UPGRADE CV: Copies internship skills to student profile
     */
    @Transactional
    public ApplicationResponse completeApplication(UUID applicationId, UpdateApplicationStatusRequest request) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (application.getStatus() != ApplicationStatus.EN_PROCESO) {
            throw new RuntimeException("Solo puedes completar pasantías en estado EN_PROCESO");
        }

        application.setStatus(ApplicationStatus.COMPLETADA);
        if (request.getFeedback() != null) {
            application.setFeedback(request.getFeedback());
        }

        Application saved = applicationRepository.save(application);

        // AUTO-UPGRADE CV: Copy internship skills to student profile
        StudentProfile studentProfile = application.getStudentProfile();
        List<String> internshipSkills = application.getInternship().getRequiredSkills();

        if (internshipSkills != null && !internshipSkills.isEmpty()) {
            List<String> currentSkills = studentProfile.getSkills();
            if (currentSkills == null) {
                currentSkills = new ArrayList<>();
            }

            // Merge and deduplicate skills
            HashSet<String> mergedSkills = new HashSet<>(currentSkills);
            mergedSkills.addAll(internshipSkills);

            studentProfile.setSkills(new ArrayList<>(mergedSkills));
            studentProfileRepository.save(studentProfile);
        }

        return applicationMapper.toResponse(saved);
    }

    /**
     * Get applications by student
     */
    public List<ApplicationResponse> getApplicationsByStudent(UUID studentProfileId) {
        return applicationRepository.findByStudentProfileId(studentProfileId)
                .stream()
                .map(applicationMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get applications for an internship
     */
    public List<ApplicationResponse> getApplicationsByInternship(UUID internshipId) {
        return applicationRepository.findByInternshipId(internshipId)
                .stream()
                .map(applicationMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get application by ID
     */
    public ApplicationResponse getApplicationById(UUID applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        return applicationMapper.toResponse(application);
    }
}
