package az.iktex.organization.structure.testders.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import az.iktex.organization.structure.testders.request.EmployeeRequest;
import az.iktex.organization.structure.testders.request.TransferRequest;
import az.iktex.organization.structure.testders.response.EmployeeResponse;
import az.iktex.organization.structure.testders.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "Employee", description = "Employee management")
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @Operation(
            summary = "Create employee",
            description = "Invalid request returns 400 (ERR-1000/ERR-1002), missing department 404 (ERR-2200), duplicate employee 409 (ERR-2301)."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponse create(@Valid @RequestBody EmployeeRequest request) {
        return employeeService.create(request);
    }

    @Operation(
            summary = "Get employee by ID",
            description = "Missing employee returns 404 (ERR-2300)."
    )
    @GetMapping("/{id}")
    public EmployeeResponse getById(@PathVariable("id") Long id) {
        return employeeService.getById(id);
    }

    @Operation(
            summary = "List employees by department",
            description = "Missing department returns 404 (ERR-2200)."
    )
    @GetMapping("/department/{departmentId}")
    public List<EmployeeResponse> getByDepartment(@PathVariable("departmentId") Long departmentId) {
        return employeeService.getByDepartment(departmentId);
    }

    @Operation(
            summary = "Find employees by salary range",
            description = "Missing parameter returns 400 (ERR-1004), invalid range 400 (ERR-2302)."
    )
    @GetMapping("/salary-range")
    public List<EmployeeResponse> getBySalaryRange(
            @RequestParam("min") BigDecimal min, @RequestParam("max") BigDecimal max) {
        return employeeService.getBySalaryRange(min, max);
    }

    @Operation(
            summary = "Get department average salary",
            description = "Missing department returns 404 (ERR-2200)."
    )
    @GetMapping("/department/{id}/average-salary")
    public Double getAverageSalary(@PathVariable("id") Long id) {
        return employeeService.getAverageSalary(id);
    }

    @Operation(
            summary = "Update employee",
            description = "Missing employee or department returns 404 (ERR-2300/ERR-2200), duplicate employee 409 (ERR-2301), invalid request 400 (ERR-1000/ERR-1002)."
    )
    @PutMapping("/{id}")
    public EmployeeResponse update(@PathVariable("id") Long id, @Valid @RequestBody EmployeeRequest request) {
        return employeeService.update(id, request);
    }

    @Operation(
            summary = "Delete employee",
            description = "Missing employee returns 404 (ERR-2300)."
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        employeeService.delete(id);
    }

    @Operation(
            summary = "Transfer employee and raise salary",
            description = "Missing employee or department returns 404 (ERR-2300/ERR-2200), invalid request 400 (ERR-1000/ERR-1002), salary limit 409 (ERR-2303)."
    )
    @PostMapping("/{id}/transfer")
    public EmployeeResponse transfer(
            @PathVariable("id") Long id,
            @Valid @RequestBody TransferRequest request
    ) {
        return employeeService.transfer(id, request);
    }
}
