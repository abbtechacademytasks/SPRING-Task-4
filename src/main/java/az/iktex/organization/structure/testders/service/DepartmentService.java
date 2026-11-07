package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.exception.NotFoundException;
import az.iktex.organization.structure.testders.enums.ErrorCode;
import az.iktex.organization.structure.testders.exception.BusinessException;
import az.iktex.organization.structure.testders.exception.AlreadyExistsException;
import az.iktex.organization.structure.testders.request.DepartmentRequest;
import az.iktex.organization.structure.testders.response.DepartmentResponse;
import az.iktex.organization.structure.testders.entity.Department;
import az.iktex.organization.structure.testders.mapper.DepartmentMapper;
import az.iktex.organization.structure.testders.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    @Transactional(propagation = Propagation.REQUIRED)
    public DepartmentResponse create(DepartmentRequest request) {
        if (departmentRepository.existsByName(request.name())) {
            throw new AlreadyExistsException(ErrorCode.DEPARTMENT_ALREADY_EXISTS);
        }
        Department department = departmentMapper.toEntity(request);
        return departmentMapper.toResponse(departmentRepository.save(department));
    }

    public DepartmentResponse getById(Long id) {
        Department department = departmentRepository.findByIdWithEmployees(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.DEPARTMENT_NOT_FOUND, id));
        return departmentMapper.toDetailResponse(department);
    }

    public List<DepartmentResponse> getAll() {
        return departmentMapper.toResponseList(departmentRepository.findAll());
    }

    public List<DepartmentResponse> getAllWithoutEmployees() {
        return departmentMapper.toResponseList(departmentRepository.findAllWithoutEmployees());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public DepartmentResponse update(Long id, DepartmentRequest request) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.DEPARTMENT_NOT_FOUND, id));
        if (departmentRepository.existsByNameAndIdNot(request.name(), id)) {
            throw new AlreadyExistsException(ErrorCode.DEPARTMENT_NAME_USED);
        }
        departmentMapper.updateEntity(request, department);
        return departmentMapper.toResponse(department);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.DEPARTMENT_NOT_FOUND, id));

        if (departmentRepository.hasEmployees(id)) {
            throw new BusinessException(ErrorCode.DEPARTMENT_HAS_EMPLOYEES);
        }

        departmentRepository.delete(department);
    }
}
