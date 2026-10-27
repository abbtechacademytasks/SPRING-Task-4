package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.request.EmployeeRequest;
import az.iktex.organization.structure.testders.response.EmployeeResponse;
import az.iktex.organization.structure.testders.entity.Department;
import az.iktex.organization.structure.testders.entity.Employee;
import az.iktex.organization.structure.testders.mapper.EmployeeMapper;
import az.iktex.organization.structure.testders.repository.DepartmentRepository;
import az.iktex.organization.structure.testders.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;

    @Transactional
    public EmployeeResponse create(EmployeeRequest request) {
        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Department not found: " + request.departmentId()));

        if (employeeRepository.existsByNameAndSurnameAndDepartmentId(
                request.name(), request.surname(), request.departmentId()
        )) {
            throw new IllegalStateException("Employee with the same name and surname already exists in this department");
        }

        Employee employee = employeeMapper.toEntity(request);
        department.addEmployee(employee);
        Employee savedEmployee = employeeRepository.save(employee);

        return employeeMapper.toResponse(savedEmployee);
    }

    public EmployeeResponse getById(Long id) {
        Employee employee = employeeRepository.findByIdWithDepartment(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found: " + id));
        return employeeMapper.toResponse(employee);
    }

    public List<EmployeeResponse> getByDepartment(Long departmentId) {
        if (!departmentRepository.existsById(departmentId)) {
            throw new EntityNotFoundException("Department not found: " + departmentId);
        }
        return employeeMapper.toResponseList(
                employeeRepository.findAllByDepartmentIdWithDepartment(departmentId));
    }

    @Transactional
    public void delete(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found: " + id));
        employeeRepository.delete(employee);
    }

    @Transactional
    public EmployeeResponse update(Long id, EmployeeRequest request) {
        Employee employee = employeeRepository.findByIdWithDepartment(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found: " + id));

        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new EntityNotFoundException("Department not found: " + request.departmentId()));

        if (employeeRepository.existsByNameAndSurnameAndDepartmentIdAndIdNot(
                request.name(), request.surname(), request.departmentId(), id)) {
            throw new IllegalStateException("Employee with the same name and surname already exists in this department");
        }
        employeeMapper.updateEntity(request, employee);
        employee.setDepartment(department);

        return employeeMapper.toResponse(employee);
    }

    public List<EmployeeResponse> getBySalaryRange(BigDecimal min, BigDecimal max) {
        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("Minimum salary cannot be greater than maximum salary");
        }

        return employeeMapper.toResponseList(employeeRepository.findAllBySalaryBetween(min, max));
    }

    public Double getAverageSalary(Long departmentId) {
        if (!departmentRepository.existsById(departmentId)) {
            throw new EntityNotFoundException("Department not found: " + departmentId);
        }

        Double averageSalary = employeeRepository.findAverageSalaryByDepartmentId(departmentId);
        return averageSalary != null ? averageSalary : 0.0;
    }
}
