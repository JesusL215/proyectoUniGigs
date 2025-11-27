package com.proyecto.unigigs.repository;

import com.proyecto.unigigs.model.Internship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InternshipRepository extends JpaRepository<Internship, UUID> {

    List<Internship> findByCompanyProfileId(UUID companyProfileId);

    List<Internship> findByIsActiveTrue();

    List<Internship> findByCompanyProfileIdAndIsActiveTrue(UUID companyProfileId);
}
