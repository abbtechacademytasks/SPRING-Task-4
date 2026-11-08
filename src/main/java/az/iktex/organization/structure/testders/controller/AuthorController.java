package az.iktex.organization.structure.testders.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import az.iktex.organization.structure.testders.request.AuthorRequest;
import az.iktex.organization.structure.testders.response.AuthorResponse;
import az.iktex.organization.structure.testders.response.BookResponse;
import az.iktex.organization.structure.testders.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Author", description = "Author management")
@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @Operation(
            summary = "Create author",
            description = "Invalid request returns 400 (ERR-1000/ERR-1002), duplicate author 409 (ERR-2401)."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorResponse create(@Valid @RequestBody AuthorRequest request) {
        return authorService.create(request);
    }

    @Operation(
            summary = "Get author by ID",
            description = "Missing author returns 404 (ERR-2400)."
    )
    @GetMapping("/{id}")
    public AuthorResponse getById(@PathVariable("id") Long id) {
        return authorService.getById(id);
    }

    @Operation(
            summary = "List authors",
            description = "Optional country filter limits the result."
    )
    @GetMapping
    public List<AuthorResponse> getAll(@RequestParam(name = "country", required = false) String country) {
        return authorService.getAll(country);
    }

    @Operation(
            summary = "List authors without books",
            description = "Returns authors with no book links."
    )
    @GetMapping("/without-books")
    public List<AuthorResponse> getAllWithoutBooks() {
        return authorService.getAllWithoutBooks();
    }

    @Operation(
            summary = "List books by author",
            description = "Missing author returns 404 (ERR-2400)."
    )
    @GetMapping("/{id}/books")
    public List<BookResponse> getBooks(@PathVariable("id") Long id) {
        return authorService.getBooks(id);
    }

    @Operation(
            summary = "Update author",
            description = "Missing author returns 404 (ERR-2400), duplicate author 409 (ERR-2401), invalid request 400 (ERR-1000/ERR-1002)."
    )
    @PutMapping("/{id}")
    public AuthorResponse update(@PathVariable("id") Long id, @Valid @RequestBody AuthorRequest request) {
        return authorService.update(id, request);
    }

    @Operation(
            summary = "Delete author",
            description = "Missing author returns 404 (ERR-2400)."
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        authorService.delete(id);
    }
}
