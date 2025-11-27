package com.proyecto.unigigs.mapper;

import com.proyecto.unigigs.dto.internship.CreateInternshipRequest;
import com.proyecto.unigigs.dto.internship.InternshipResponse;
import com.proyecto.unigigs.dto.internship.UpdateInternshipRequest;
import com.proyecto.unigigs.model.Internship;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface InternshipMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "companyProfile", ignore = true)
    @Mapping(target = "applications", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    Internship toEntity(CreateInternshipRequest request);

    @Mapping(source = "companyProfile.id", target = "companyId")
    @Mapping(source = "companyProfile.companyName", target = "companyName")
    @Mapping(source = "companyProfile.industry", target = "industry")
    InternshipResponse toResponse(Internship internship);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "companyProfile", ignore = true)
    @Mapping(target = "applications", ignore = true)
    void updateEntity(UpdateInternshipRequest request, @MappingTarget Internship internship);
}
