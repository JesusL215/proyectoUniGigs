package com.proyecto.unigigs.repository;

import com.proyecto.unigigs.model.Application;
import com.proyecto.unigigs.model.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {

    List<Application> findByStudentProfileId(UUID studentProfileId);

    List<Application> findByInternshipId(UUID internshipId);

    // For Rule of 5: count active applications (PENDIENTE + SELECCIONADO)
    @Query("SELECT COUNT(a) FROM Application a WHERE a.studentProfile.id = :studentProfileId " +
            "AND a.status IN :statuses")
    long countByStudentProfileIdAndStatusIn(
            @Param("studentProfileId") UUID studentProfileId,
            @Param("statuses") List<ApplicationStatus> statuses);

    // For exclusivity check: verify if student has an EN_PROCESO application
    boolean existsByStudentProfileIdAndStatus(UUID studentProfileId, ApplicationStatus status);

    // Get all applications by student with specific statuses
    List<Application> findByStudentProfileIdAndStatusIn(UUID studentProfileId, List<ApplicationStatus> statuses);
}
