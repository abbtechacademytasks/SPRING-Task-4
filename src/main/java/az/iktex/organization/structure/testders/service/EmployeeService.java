package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.exception.NotFoundException;
import az.iktex.organization.structure.testders.exception.InvalidRequestException;
import az.iktex.organization.structure.testders.enums.ErrorCode;
import az.iktex.organization.structure.testders.exception.AlreadyExistsException;
import az.iktex.organization.structure.testders.enums.AuditStatus;
import az.iktex.organization.structure.testders.request.EmployeeRequest;
import az.iktex.organization.structure.testders.request.TransferRequest;
import az.iktex.organization.structure.testders.response.EmployeeResponse;
import az.iktex.organization.structure.testders.entity.Department;
import az.iktex.organization.structure.testders.entity.Employee;
import az.iktex.organization.structure.testders.mapper.EmployeeMapper;
import az.iktex.organization.structure.testders.repository.DepartmentRepository;
import az.iktex.organization.structure.testders.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
    private final SalaryService salaryService;
    private final AuditLogService auditLogService;

    @Transactional(propagation = Propagation.REQUIRED)
    public EmployeeResponse create(EmployeeRequest request) {
        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() ->
                        new NotFoundException(ErrorCode.DEPARTMENT_NOT_FOUND, request.departmentId()));

        if (employeeRepository.existsByNameAndSurnameAndDepartmentId(
                request.name(), request.surname(), request.departmentId()
        )) {
            throw new AlreadyExistsException(ErrorCode.EMPLOYEE_ALREADY_EXISTS);
        }

        Employee employee = employeeMapper.toEntity(request);
        department.addEmployee(employee);
        Employee savedEmployee = employeeRepository.save(employee);

        return employeeMapper.toResponse(savedEmployee);
    }

    public EmployeeResponse getById(Long id) {
        Employee employee = employeeRepository.findByIdWithDepartment(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.EMPLOYEE_NOT_FOUND, id));
        return employeeMapper.toResponse(employee);
    }

    public List<EmployeeResponse> getByDepartment(Long departmentId) {
        if (!departmentRepository.existsById(departmentId)) {
            throw new NotFoundException(ErrorCode.DEPARTMENT_NOT_FOUND, departmentId);
        }
        return employeeMapper.toResponseList(
                employeeRepository.findAllByDepartmentIdWithDepartment(departmentId));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.EMPLOYEE_NOT_FOUND, id));
        employeeRepository.delete(employee);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public EmployeeResponse update(Long id, EmployeeRequest request) {
        Employee employee = employeeRepository.findByIdWithDepartment(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.EMPLOYEE_NOT_FOUND, id));

        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.DEPARTMENT_NOT_FOUND, request.departmentId()));

        if (employeeRepository.existsByNameAndSurnameAndDepartmentIdAndIdNot(
                request.name(), request.surname(), request.departmentId(), id)) {
            throw new AlreadyExistsException(ErrorCode.EMPLOYEE_ALREADY_EXISTS);
        }
        employeeMapper.updateEntity(request, employee);
        employee.setDepartment(department);

        return employeeMapper.toResponse(employee);
    }

    public List<EmployeeResponse> getBySalaryRange(BigDecimal min, BigDecimal max) {
        if (min.compareTo(max) > 0) {
            throw new InvalidRequestException(ErrorCode.EMPLOYEE_SALARY_RANGE);
        }

        return employeeMapper.toResponseList(employeeRepository.findAllBySalaryBetween(min, max));
    }

    public Double getAverageSalary(Long departmentId) {
        if (!departmentRepository.existsById(departmentId)) {
            throw new NotFoundException(ErrorCode.DEPARTMENT_NOT_FOUND, departmentId);
        }

        Double averageSalary = employeeRepository.findAverageSalaryByDepartmentId(departmentId);
        return averageSalary != null ? averageSalary : 0.0;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public EmployeeResponse transfer(Long id, TransferRequest request) {
        if (request.departmentId() == null) {
            throw new InvalidRequestException(ErrorCode.MISSING_PARAMETER);
        }

        Employee employee = employeeRepository.findByIdWithDepartment(id)
                .orElseThrow(() ->
                        new NotFoundException(ErrorCode.EMPLOYEE_NOT_FOUND, id));

        Department department = departmentRepository
                .findById(request.departmentId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.DEPARTMENT_NOT_FOUND, request.departmentId()));

        try {
            employee.setDepartment(department);
            salaryService.raise(employee, request.raisePercent());
        } catch (RuntimeException exception) {
            auditLogService.log(
                    "TRANSFER",
                    "Employee",
                    id,
                    AuditStatus.FAILED,
                    exception.getMessage()
            );

            throw exception;
        }

        auditLogService.log(
                "TRANSFER",
                "Employee",
                id,
                AuditStatus.SUCCESS,
                "Employee transferred and salary increased"
        );

        return employeeMapper.toResponse(employee);
    }
}
