package az.iktex.organization.structure.testders.mapper;

import az.iktex.organization.structure.testders.request.PassportRequest;
import az.iktex.organization.structure.testders.response.PassportResponse;
import az.iktex.organization.structure.testders.entity.Passport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PassportMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    Passport toEntity(PassportRequest request);

    PassportResponse toResponse(Passport passport);

    List<PassportResponse> toResponseList(List<Passport> passports);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateEntity(
            PassportRequest request,
            @MappingTarget Passport passport
    );
}
