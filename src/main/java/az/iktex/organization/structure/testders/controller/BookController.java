package az.iktex.organization.structure.testders.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import az.iktex.organization.structure.testders.request.BookRequest;
import az.iktex.organization.structure.testders.response.BookResponse;
import az.iktex.organization.structure.testders.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "Book", description = "Book management")
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @Operation(
            summary = "Create book",
            description = "Invalid request returns 400 (ERR-1000/ERR-1002), duplicate title 409 (ERR-2501)."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse create(@Valid @RequestBody BookRequest request) {
        return bookService.create(request);
    }

    @Operation(
            summary = "Get book by ID",
            description = "Missing book returns 404 (ERR-2500)."
    )
    @GetMapping("/{id}")
    public BookResponse getById(@PathVariable("id") Long id) {
        return bookService.getById(id);
    }

    @Operation(
            summary = "List books",
            description = "Optional title filter limits the result."
    )
    @GetMapping
    public List<BookResponse> getAll(@RequestParam(name = "title", required = false) String title) {
        return bookService.getAll(title);
    }

    @Operation(
            summary = "Find books below price",
            description = "Missing parameter returns 400 (ERR-1004), invalid price type 400 (ERR-1003)."
    )
    @GetMapping("/cheaper-than")
    public List<BookResponse> getCheaperThan(@RequestParam("price") BigDecimal price) {
        return bookService.getCheaperThan(price);
    }

    @Operation(
            summary = "Update book",
            description = "Missing book returns 404 (ERR-2500), duplicate title 409 (ERR-2501), invalid request 400 (ERR-1000/ERR-1002)."
    )
    @PutMapping("/{id}")
    public BookResponse update(@PathVariable("id") Long id, @Valid @RequestBody BookRequest request) {
        return bookService.update(id, request);
    }

    @Operation(
            summary = "Delete book",
            description = "Missing book returns 404 (ERR-2500)."
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        bookService.delete(id);
    }

    @Operation(
            summary = "Link author to book",
            description = "Missing author or book returns 404 (ERR-2400/ERR-2500), existing link 409 (ERR-2502)."
    )
    @PostMapping("/{bookId}/authors/{authorId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addAuthor(@PathVariable("bookId") Long bookId, @PathVariable("authorId") Long authorId) {
        bookService.addAuthor(bookId, authorId);
    }

    @Operation(
            summary = "Unlink author from book",
            description = "Missing link returns 404 (ERR-2503)."
    )
    @DeleteMapping("/{bookId}/authors/{authorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAuthor(@PathVariable("bookId") Long bookId, @PathVariable("authorId") Long authorId) {
        bookService.removeAuthor(bookId, authorId);
    }
}
