package az.iktex.organization.structure.testders.controller;

import az.iktex.organization.structure.testders.request.AuthorRequest;
import az.iktex.organization.structure.testders.response.AuthorResponse;
import az.iktex.organization.structure.testders.response.BookResponse;
import az.iktex.organization.structure.testders.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorResponse create(@RequestBody AuthorRequest request) {
        return authorService.create(request);
    }

    @GetMapping("/{id}")
    public AuthorResponse getById(@PathVariable("id") Long id) {
        return authorService.getById(id);
    }

    @GetMapping
    public List<AuthorResponse> getAll(@RequestParam(name = "country", required = false) String country) {
        return authorService.getAll(country);
    }

    @GetMapping("/without-books")
    public List<AuthorResponse> getAllWithoutBooks() {
        return authorService.getAllWithoutBooks();
    }

    @GetMapping("/{id}/books")
    public List<BookResponse> getBooks(@PathVariable("id") Long id) {
        return authorService.getBooks(id);
    }

    @PutMapping("/{id}")
    public AuthorResponse update(@PathVariable("id") Long id, @RequestBody AuthorRequest request) {
        return authorService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        authorService.delete(id);
    }
}
