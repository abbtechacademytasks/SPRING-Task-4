package az.iktex.organization.structure.testders.controller;

import az.iktex.organization.structure.testders.request.DepartmentRequest;
import az.iktex.organization.structure.testders.response.DepartmentResponse;
import az.iktex.organization.structure.testders.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DepartmentResponse create(@RequestBody DepartmentRequest request) {
        return departmentService.create(request);
    }

    @GetMapping("/{id}")
    public DepartmentResponse getById(@PathVariable("id") Long id) {
        return departmentService.getById(id);
    }

    @GetMapping
    public List<DepartmentResponse> getAll() {
        return departmentService.getAll();
    }

    @GetMapping("/without-employees")
    public List<DepartmentResponse> getAllWithoutEmployees() {
        return departmentService.getAllWithoutEmployees();
    }

    @PutMapping("/{id}")
    public DepartmentResponse update(@PathVariable("id") Long id, @RequestBody DepartmentRequest request) {
        return departmentService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        departmentService.delete(id);
    }
}
