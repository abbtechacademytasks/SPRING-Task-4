package az.iktex.organization.structure.testders.controller;

import az.iktex.organization.structure.testders.request.EmployeeRequest;
import az.iktex.organization.structure.testders.response.EmployeeResponse;
import az.iktex.organization.structure.testders.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponse create(@RequestBody EmployeeRequest request) {
        return employeeService.create(request);
    }

    @GetMapping("/{id}")
    public EmployeeResponse getById(@PathVariable("id") Long id) {
        return employeeService.getById(id);
    }

    @GetMapping("/department/{departmentId}")
    public List<EmployeeResponse> getByDepartment(@PathVariable("departmentId") Long departmentId) {
        return employeeService.getByDepartment(departmentId);
    }

    @GetMapping("/salary-range")
    public List<EmployeeResponse> getBySalaryRange(
            @RequestParam("min") BigDecimal min, @RequestParam("max") BigDecimal max) {
        return employeeService.getBySalaryRange(min, max);
    }

    @GetMapping("/department/{id}/average-salary")
    public Double getAverageSalary(@PathVariable("id") Long id) {
        return employeeService.getAverageSalary(id);
    }

    @PutMapping("/{id}")
    public EmployeeResponse update(@PathVariable("id") Long id, @RequestBody EmployeeRequest request) {
        return employeeService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        employeeService.delete(id);
    }
}
