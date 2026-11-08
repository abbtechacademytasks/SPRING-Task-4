package az.iktex.organization.structure.testders.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import az.iktex.organization.structure.testders.request.DepartmentRequest;
import az.iktex.organization.structure.testders.response.DepartmentResponse;
import az.iktex.organization.structure.testders.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Department", description = "Department management")
@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @Operation(
            summary = "Create department",
            description = "Invalid request returns 400 (ERR-1000/ERR-1002), duplicate name 409 (ERR-2201)."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DepartmentResponse create(@Valid @RequestBody DepartmentRequest request) {
        return departmentService.create(request);
    }

    @Operation(
            summary = "Get department by ID",
            description = "Missing department returns 404 (ERR-2200)."
    )
    @GetMapping("/{id}")
    public DepartmentResponse getById(@PathVariable("id") Long id) {
        return departmentService.getById(id);
    }

    @Operation(
            summary = "List departments",
            description = "Returns all departments."
    )
    @GetMapping
    public List<DepartmentResponse> getAll() {
        return departmentService.getAll();
    }

    @Operation(
            summary = "List departments without employees",
            description = "Returns departments with no employees."
    )
    @GetMapping("/without-employees")
    public List<DepartmentResponse> getAllWithoutEmployees() {
        return departmentService.getAllWithoutEmployees();
    }

    @Operation(
            summary = "Update department",
            description = "Missing department returns 404 (ERR-2200), used name 409 (ERR-2202), invalid request 400 (ERR-1000/ERR-1002)."
    )
    @PutMapping("/{id}")
    public DepartmentResponse update(@PathVariable("id") Long id, @Valid @RequestBody DepartmentRequest request) {
        return departmentService.update(id, request);
    }

    @Operation(
            summary = "Delete department",
            description = "Missing department returns 404 (ERR-2200), existing employees 409 (ERR-2203)."
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        departmentService.delete(id);
    }
}
