package az.iktex.organization.structure.testders.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import az.iktex.organization.structure.testders.request.PassportRequest;
import az.iktex.organization.structure.testders.request.UserRequest;
import az.iktex.organization.structure.testders.response.PassportResponse;
import az.iktex.organization.structure.testders.response.UserResponse;
import az.iktex.organization.structure.testders.enums.Gender;
import az.iktex.organization.structure.testders.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "User", description = "User management")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Create user",
            description = "Invalid request returns 400 (ERR-1000/ERR-1002), existing email 409 (ERR-2601)."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody UserRequest request) {
        return userService.create(request);
    }

    @Operation(
            summary = "Get user by ID",
            description = "Missing user returns 404 (ERR-2600)."
    )
    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable("id") Long id) {
        return userService.getById(id);
    }

    @Operation(
            summary = "List users",
            description = "Optional gender filter limits the result. Invalid gender returns 400 (ERR-1003)."
    )
    @GetMapping
    public List<UserResponse> getAll(
            @RequestParam(name = "gender", required = false) Gender gender
    ) {
        return userService.getAll(gender);
    }

    @Operation(
            summary = "Find user by email",
            description = "Missing email parameter returns 400 (ERR-1004), missing user 404 (ERR-2600)."
    )
    @GetMapping("/by-email")
    public UserResponse getByEmail(@RequestParam("email") String email) {
        return userService.getByEmail(email);
    }

    @Operation(
            summary = "List users without passport",
            description = "Returns users without a passport."
    )
    @GetMapping("/without-passport")
    public List<UserResponse> getAllWithoutPassport() {
        return userService.getAllWithoutPassport();
    }

    @Operation(
            summary = "Update user",
            description = "Missing user returns 404 (ERR-2600), used email 409 (ERR-2602), invalid request 400 (ERR-1000/ERR-1002)."
    )
    @PutMapping("/{id}")
    public UserResponse update(
            @PathVariable("id") Long id,
            @Valid @RequestBody UserRequest request
    ) {
        return userService.update(id, request);
    }

    @Operation(
            summary = "Delete user",
            description = "Missing user returns 404 (ERR-2600)."
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        userService.delete(id);
    }

    @Operation(
            summary = "Add user passport",
            description = "Missing user returns 404 (ERR-2600), existing passport or serial 409 (ERR-2603/ERR-2605), invalid request 400 (ERR-1000/ERR-1002)."
    )
    @PostMapping("/{id}/passport")
    @ResponseStatus(HttpStatus.CREATED)
    public PassportResponse addPassport(
            @PathVariable("id") Long id,
            @Valid @RequestBody PassportRequest request
    ) {
        return userService.addPassport(id, request);
    }

    @Operation(
            summary = "Get user passport",
            description = "Missing user or passport returns 404 (ERR-2600/ERR-2604)."
    )
    @GetMapping("/{id}/passport")
    public PassportResponse getPassport(@PathVariable("id") Long id) {
        return userService.getPassport(id);
    }

    @Operation(
            summary = "Delete user passport",
            description = "Missing user or passport returns 404 (ERR-2600/ERR-2604)."
    )
    @DeleteMapping("/{id}/passport")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePassport(@PathVariable("id") Long id) {
        userService.deletePassport(id);
    }
}
