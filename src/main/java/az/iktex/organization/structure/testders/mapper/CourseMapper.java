package az.iktex.organization.structure.testders.mapper;

import az.iktex.organization.structure.testders.request.CourseRequest;
import az.iktex.organization.structure.testders.response.CourseResponse;
import az.iktex.organization.structure.testders.entity.Course;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "students", ignore = true)
    Course toEntity(CourseRequest request);

    CourseResponse toResponse(Course course);

    List<CourseResponse> toResponseList(List<Course> courses);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "students", ignore = true)
    void updateEntity(CourseRequest request, @MappingTarget Course course);
}
