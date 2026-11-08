package az.iktex.organization.structure.testders.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import az.iktex.organization.structure.testders.request.CourseRequest;
import az.iktex.organization.structure.testders.response.CourseResponse;
import az.iktex.organization.structure.testders.response.StudentResponse;
import az.iktex.organization.structure.testders.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Course", description = "Course management")
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @Operation(
            summary = "Create course",
            description = "Invalid request returns 400 (ERR-1000/ERR-1002), duplicate title 409 (ERR-2101)."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseResponse create(@Valid @RequestBody CourseRequest request) {
        return courseService.create(request);
    }

    @Operation(
            summary = "Get course by ID",
            description = "Missing course returns 404 (ERR-2100)."
    )
    @GetMapping("/{id}")
    public CourseResponse getById(@PathVariable("id") Long id) {
        return courseService.getById(id);
    }

    @Operation(
            summary = "List courses",
            description = "Returns all courses."
    )
    @GetMapping
    public List<CourseResponse> getAll() {
        return courseService.getAll();
    }

    @Operation(
            summary = "List courses of student",
            description = "Missing student returns 404 (ERR-2000)."
    )
    @GetMapping("/student/{studentId}")
    public List<CourseResponse> getByStudent(@PathVariable("studentId") Long studentId) {
        return courseService.getByStudent(studentId);
    }

    @Operation(
            summary = "List students of course",
            description = "Missing course returns 404 (ERR-2100)."
    )
    @GetMapping("/{id}/students")
    public List<StudentResponse> getStudents(@PathVariable("id") Long id) {
        return courseService.getStudents(id);
    }

    @Operation(
            summary = "Count students of course",
            description = "Missing course returns 404 (ERR-2100)."
    )
    @GetMapping("/{id}/students/count")
    public Integer getStudentCount(@PathVariable("id") Long id) {
        return courseService.getStudentCount(id);
    }

    @Operation(
            summary = "Update course",
            description = "Missing course returns 404 (ERR-2100), used title 409 (ERR-2102), invalid request 400 (ERR-1000/ERR-1002)."
    )
    @PutMapping("/{id}")
    public CourseResponse update(@PathVariable("id") Long id, @Valid @RequestBody CourseRequest request) {
        return courseService.update(id, request);
    }

    @Operation(
            summary = "Delete course",
            description = "Missing course returns 404 (ERR-2100)."
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        courseService.delete(id);
    }
}
