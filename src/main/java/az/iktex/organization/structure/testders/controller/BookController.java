package az.iktex.organization.structure.testders.controller;

import az.iktex.organization.structure.testders.request.BookRequest;
import az.iktex.organization.structure.testders.response.BookResponse;
import az.iktex.organization.structure.testders.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse create(@RequestBody BookRequest request) {
        return bookService.create(request);
    }

    @GetMapping("/{id}")
    public BookResponse getById(@PathVariable("id") Long id) {
        return bookService.getById(id);
    }

    @GetMapping
    public List<BookResponse> getAll(@RequestParam(name = "title", required = false) String title) {
        return bookService.getAll(title);
    }

    @GetMapping("/cheaper-than")
    public List<BookResponse> getCheaperThan(@RequestParam("price") BigDecimal price) {
        return bookService.getCheaperThan(price);
    }

    @PutMapping("/{id}")
    public BookResponse update(@PathVariable("id") Long id, @RequestBody BookRequest request) {
        return bookService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        bookService.delete(id);
    }

    @PostMapping("/{bookId}/authors/{authorId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addAuthor(@PathVariable("bookId") Long bookId, @PathVariable("authorId") Long authorId) {
        bookService.addAuthor(bookId, authorId);
    }

    @DeleteMapping("/{bookId}/authors/{authorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAuthor(@PathVariable("bookId") Long bookId, @PathVariable("authorId") Long authorId) {
        bookService.removeAuthor(bookId, authorId);
    }
}
