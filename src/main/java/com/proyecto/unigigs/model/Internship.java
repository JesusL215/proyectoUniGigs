package com.proyecto.unigigs.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "internships")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Internship extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "company_profile_id", nullable = false)
    private CompanyProfile companyProfile;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @ElementCollection
    @CollectionTable(name = "internship_required_skills", joinColumns = @JoinColumn(name = "internship_id"))
    @Column(name = "skill")
    @Builder.Default
    private List<String> requiredSkills = new ArrayList<>();

    @Column(nullable = false)
    private Integer duration; // Duration in weeks

    @Column(nullable = false)
    private String location;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @OneToMany(mappedBy = "internship", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Application> applications = new ArrayList<>();
}
