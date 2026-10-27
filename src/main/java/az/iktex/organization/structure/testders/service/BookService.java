package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.request.BookRequest;
import az.iktex.organization.structure.testders.response.BookResponse;
import az.iktex.organization.structure.testders.entity.Author;
import az.iktex.organization.structure.testders.entity.AuthorBook;
import az.iktex.organization.structure.testders.entity.Book;
import az.iktex.organization.structure.testders.mapper.BookMapper;
import az.iktex.organization.structure.testders.repository.AuthorBookRepository;
import az.iktex.organization.structure.testders.repository.AuthorRepository;
import az.iktex.organization.structure.testders.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

    @Transactional
    public BookResponse create(BookRequest request) {
        if (bookRepository.existsByTitle(request.title())) {
            throw new IllegalStateException("Book title already exists");
        }
        Book book = bookMapper.toEntity(request);
        return bookMapper.toResponse(bookRepository.save(book));
    }

    public BookResponse getById(Long id) {
        Book book = bookRepository.findByIdWithAuthors(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found: " + id));
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

    @Transactional
    public BookResponse update(Long id, BookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found: " + id));
        if (bookRepository.existsByTitleAndIdNot(request.title(), id)) {
            throw new IllegalStateException("Book title already exists");
        }
        bookMapper.updateEntity(request, book);
        return bookMapper.toResponse(book);
    }

    @Transactional
    public void delete(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found: " + id));
        bookRepository.delete(book);
    }

    @Transactional
    public void addAuthor(Long bookId, Long authorId) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author not found: " + authorId));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found: " + bookId));

        if (authorBookRepository.existsByAuthorIdAndBookId(authorId, bookId)) {
            throw new IllegalStateException("Author is already associated with this book");
        }

        AuthorBook link = new AuthorBook();
        link.setAuthor(author);
        link.setBook(book);

        author.getAuthorBooks().add(link);
        book.getAuthorBooks().add(link);

        authorBookRepository.save(link);
    }

    @Transactional
    public void removeAuthor(Long bookId, Long authorId) {
        AuthorBook link = authorBookRepository
                .findByAuthorIdAndBookId(authorId, bookId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Author-book link not found"));

        authorBookRepository.delete(link);
    }
}
