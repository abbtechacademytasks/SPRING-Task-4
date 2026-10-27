package az.iktex.organization.structure.testders.controller;

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

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@RequestBody UserRequest request) {
        return userService.create(request);
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable("id") Long id) {
        return userService.getById(id);
    }

    @GetMapping
    public List<UserResponse> getAll(
            @RequestParam(name = "gender", required = false) Gender gender
    ) {
        return userService.getAll(gender);
    }

    @GetMapping("/by-email")
    public UserResponse getByEmail(@RequestParam("email") String email) {
        return userService.getByEmail(email);
    }

    @GetMapping("/without-passport")
    public List<UserResponse> getAllWithoutPassport() {
        return userService.getAllWithoutPassport();
    }

    @PutMapping("/{id}")
    public UserResponse update(
            @PathVariable("id") Long id,
            @RequestBody UserRequest request
    ) {
        return userService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        userService.delete(id);
    }

    @PostMapping("/{id}/passport")
    @ResponseStatus(HttpStatus.CREATED)
    public PassportResponse addPassport(
            @PathVariable("id") Long id,
            @RequestBody PassportRequest request
    ) {
        return userService.addPassport(id, request);
    }

    @GetMapping("/{id}/passport")
    public PassportResponse getPassport(@PathVariable("id") Long id) {
        return userService.getPassport(id);
    }

    @DeleteMapping("/{id}/passport")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePassport(@PathVariable("id") Long id) {
        userService.deletePassport(id);
    }
}
