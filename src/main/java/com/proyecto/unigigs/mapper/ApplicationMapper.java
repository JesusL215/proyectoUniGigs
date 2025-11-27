package com.proyecto.unigigs.mapper;

import com.proyecto.unigigs.dto.application.ApplicationResponse;
import com.proyecto.unigigs.model.Application;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {

    @Mapping(source = "studentProfile.id", target = "studentId")
    @Mapping(source = "studentProfile.user.firstName", target = "studentName", qualifiedByName = "getFullName")
    @Mapping(source = "studentProfile.user.email", target = "studentEmail")
    @Mapping(source = "studentProfile.university", target = "university")
    @Mapping(source = "studentProfile.career", target = "career")
    @Mapping(source = "internship.id", target = "internshipId")
    @Mapping(source = "internship.title", target = "internshipTitle")
    @Mapping(source = "internship.companyProfile.companyName", target = "companyName")
    ApplicationResponse toResponse(Application application);

    default String getFullName(Application application) {
        if (application.getStudentProfile() != null &&
                application.getStudentProfile().getUser() != null) {
            var user = application.getStudentProfile().getUser();
            return user.getFirstName() + " " + user.getLastName();
        }
        return "";
    }
}
