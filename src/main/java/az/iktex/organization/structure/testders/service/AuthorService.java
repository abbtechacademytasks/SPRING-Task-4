package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.exception.NotFoundException;
import az.iktex.organization.structure.testders.enums.ErrorCode;
import az.iktex.organization.structure.testders.exception.AlreadyExistsException;
import az.iktex.organization.structure.testders.request.AuthorRequest;
import az.iktex.organization.structure.testders.response.AuthorResponse;
import az.iktex.organization.structure.testders.response.BookResponse;
import az.iktex.organization.structure.testders.entity.Author;
import az.iktex.organization.structure.testders.mapper.AuthorMapper;
import az.iktex.organization.structure.testders.mapper.BookMapper;
import az.iktex.organization.structure.testders.repository.AuthorRepository;
import az.iktex.organization.structure.testders.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final AuthorMapper authorMapper;
    private final BookMapper bookMapper;

    @Transactional(propagation = Propagation.REQUIRED)
    public AuthorResponse create(AuthorRequest request) {
        if (authorRepository.existsByNameAndSurname(request.name(), request.surname())) {
            throw new AlreadyExistsException(ErrorCode.AUTHOR_ALREADY_EXISTS);
        }
        Author author = authorMapper.toEntity(request);
        return authorMapper.toResponse(authorRepository.save(author));
    }

    public AuthorResponse getById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.AUTHOR_NOT_FOUND, id));
        return authorMapper.toResponse(author);
    }

    public List<AuthorResponse> getAll(String country) {
        List<Author> authors = country == null
                ? authorRepository.findAll()
                : authorRepository.findAllByCountry(country);
        return authorMapper.toResponseList(authors);
    }

    public List<AuthorResponse> getAllWithoutBooks() {
        return authorMapper.toResponseList(authorRepository.findAllWithoutBooks());
    }

    public List<BookResponse> getBooks(Long authorId) {
        if (!authorRepository.existsById(authorId)) {
            throw new NotFoundException(ErrorCode.AUTHOR_NOT_FOUND, authorId);
        }
        return bookMapper.toResponseList(bookRepository.findAllByAuthorId(authorId));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AuthorResponse update(Long id, AuthorRequest request) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.AUTHOR_NOT_FOUND, id));
        if (authorRepository.existsByNameAndSurnameAndIdNot(request.name(), request.surname(), id)) {
            throw new AlreadyExistsException(ErrorCode.AUTHOR_ALREADY_EXISTS);
        }
        authorMapper.updateEntity(request, author);
        return authorMapper.toResponse(author);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.AUTHOR_NOT_FOUND, id));
        authorRepository.delete(author);
    }
}
