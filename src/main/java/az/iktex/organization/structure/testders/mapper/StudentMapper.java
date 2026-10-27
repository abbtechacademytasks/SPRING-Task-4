package az.iktex.organization.structure.testders.mapper;

import az.iktex.organization.structure.testders.request.StudentRequest;
import az.iktex.organization.structure.testders.response.StudentResponse;
import az.iktex.organization.structure.testders.entity.Student;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = CourseMapper.class)
public interface StudentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "courses", ignore = true)
    Student toEntity(StudentRequest request);

    @Mapping(target = "courses", ignore = true)
    StudentResponse toResponse(Student student);

    List<StudentResponse> toResponseList(List<Student> students);

    @Named("withCourses")
    StudentResponse toDetailResponse(Student student);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "courses", ignore = true)
    void updateEntity(StudentRequest request, @MappingTarget Student student);
}
