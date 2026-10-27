package az.iktex.organization.structure.testders.mapper;

import az.iktex.organization.structure.testders.request.AuthorRequest;
import az.iktex.organization.structure.testders.response.AuthorResponse;
import az.iktex.organization.structure.testders.entity.Author;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorBooks", ignore = true)
    Author toEntity(AuthorRequest request);

    AuthorResponse toResponse(Author author);

    List<AuthorResponse> toResponseList(List<Author> authors);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorBooks", ignore = true)
    void updateEntity(AuthorRequest request, @MappingTarget Author author);
}
