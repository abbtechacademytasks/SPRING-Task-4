package az.iktex.organization.structure.testders.mapper;

import az.iktex.organization.structure.testders.request.BookRequest;
import az.iktex.organization.structure.testders.response.AuthorResponse;
import az.iktex.organization.structure.testders.response.BookResponse;
import az.iktex.organization.structure.testders.entity.AuthorBook;
import az.iktex.organization.structure.testders.entity.Book;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorBooks", ignore = true)
    Book toEntity(BookRequest request);

    @Mapping(target = "authors", ignore = true)
    BookResponse toResponse(Book book);

    List<BookResponse> toResponseList(List<Book> books);

    @Named("withAuthors")
    @Mapping(target = "authors", source = "authorBooks")
    BookResponse toDetailResponse(Book book);

    @Mapping(target = "id", source = "author.id")
    @Mapping(target = "name", source = "author.name")
    @Mapping(target = "surname", source = "author.surname")
    @Mapping(target = "country", source = "author.country")
    AuthorResponse toAuthorResponse(AuthorBook authorBook);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorBooks", ignore = true)
    void updateEntity(BookRequest request, @MappingTarget Book book);
}
