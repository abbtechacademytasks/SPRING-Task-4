package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.exception.NotFoundException;
import az.iktex.organization.structure.testders.enums.ErrorCode;
import az.iktex.organization.structure.testders.exception.AlreadyExistsException;
import az.iktex.organization.structure.testders.request.BookRequest;
import az.iktex.organization.structure.testders.response.BookResponse;
import az.iktex.organization.structure.testders.entity.Author;
import az.iktex.organization.structure.testders.entity.AuthorBook;
import az.iktex.organization.structure.testders.entity.Book;
import az.iktex.organization.structure.testders.mapper.BookMapper;
import az.iktex.organization.structure.testders.repository.AuthorBookRepository;
import az.iktex.organization.structure.testders.repository.AuthorRepository;
import az.iktex.organization.structure.testders.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final AuthorBookRepository authorBookRepository;
    private final BookMapper bookMapper;

    @Transactional(propagation = Propagation.REQUIRED)
    public BookResponse create(BookRequest request) {
        if (bookRepository.existsByTitle(request.title())) {
            throw new AlreadyExistsException(ErrorCode.BOOK_ALREADY_EXISTS);
        }
        Book book = bookMapper.toEntity(request);
        return bookMapper.toResponse(bookRepository.save(book));
    }

    public BookResponse getById(Long id) {
        Book book = bookRepository.findByIdWithAuthors(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.BOOK_NOT_FOUND, id));
        return bookMapper.toDetailResponse(book);
    }

    public List<BookResponse> getAll(String title) {
        List<Book> books = title == null
                ? bookRepository.findAll()
                : bookRepository.findAllByTitleContainingIgnoreCase(title);
        return bookMapper.toResponseList(books);
    }

    public List<BookResponse> getCheaperThan(BigDecimal price) {
        return bookMapper.toResponseList(bookRepository.findAllByPriceLessThanEqualOrderByPriceAsc(price));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public BookResponse update(Long id, BookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.BOOK_NOT_FOUND, id));
        if (bookRepository.existsByTitleAndIdNot(request.title(), id)) {
            throw new AlreadyExistsException(ErrorCode.BOOK_ALREADY_EXISTS);
        }
        bookMapper.updateEntity(request, book);
        return bookMapper.toResponse(book);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.BOOK_NOT_FOUND, id));
        bookRepository.delete(book);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void addAuthor(Long bookId, Long authorId) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.AUTHOR_NOT_FOUND, authorId));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.BOOK_NOT_FOUND, bookId));

        if (authorBookRepository.existsByAuthorIdAndBookId(authorId, bookId)) {
            throw new AlreadyExistsException(ErrorCode.BOOK_AUTHOR_ALREADY_LINKED);
        }

        AuthorBook link = new AuthorBook();
        link.setAuthor(author);
        link.setBook(book);

        author.getAuthorBooks().add(link);
        book.getAuthorBooks().add(link);

        authorBookRepository.save(link);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void removeAuthor(Long bookId, Long authorId) {
        AuthorBook link = authorBookRepository
                .findByAuthorIdAndBookId(authorId, bookId)
                .orElseThrow(() ->
                        new NotFoundException(ErrorCode.BOOK_AUTHOR_NOT_LINKED));

        link.getAuthor().removeAuthorBook(link);
    }
}
