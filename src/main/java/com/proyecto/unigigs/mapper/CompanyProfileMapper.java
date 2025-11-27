package com.proyecto.unigigs.mapper;

import com.proyecto.unigigs.dto.profile.CompanyProfileRequest;
import com.proyecto.unigigs.dto.profile.CompanyProfileResponse;
import com.proyecto.unigigs.model.CompanyProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CompanyProfileMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "internships", ignore = true)
    CompanyProfile toEntity(CompanyProfileRequest request);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    CompanyProfileResponse toResponse(CompanyProfile profile);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "internships", ignore = true)
    void updateEntity(CompanyProfileRequest request, @MappingTarget CompanyProfile profile);
}
