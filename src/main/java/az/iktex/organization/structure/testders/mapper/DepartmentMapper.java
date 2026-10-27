package az.iktex.organization.structure.testders.mapper;

import az.iktex.organization.structure.testders.request.DepartmentRequest;
import az.iktex.organization.structure.testders.response.DepartmentResponse;
import az.iktex.organization.structure.testders.entity.Department;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = EmployeeMapper.class)
public interface DepartmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employees", ignore = true)
    Department toEntity(DepartmentRequest request);

    @Mapping(target = "employees", ignore = true)
    DepartmentResponse toResponse(Department department);

    List<DepartmentResponse> toResponseList(List<Department> departments);

    @Named("withEmployees")
    DepartmentResponse toDetailResponse(Department department);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employees", ignore = true)
    void updateEntity(DepartmentRequest request, @MappingTarget Department department);
}
