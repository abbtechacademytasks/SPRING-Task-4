package az.iktex.organization.structure.testders.controller;

import az.iktex.organization.structure.testders.request.CourseRequest;
import az.iktex.organization.structure.testders.response.CourseResponse;
import az.iktex.organization.structure.testders.response.StudentResponse;
import az.iktex.organization.structure.testders.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseResponse create(@RequestBody CourseRequest request) {
        return courseService.create(request);
    }

    @GetMapping("/{id}")
    public CourseResponse getById(@PathVariable("id") Long id) {
        return courseService.getById(id);
    }

    @GetMapping
    public List<CourseResponse> getAll() {
        return courseService.getAll();
    }

    @GetMapping("/student/{studentId}")
    public List<CourseResponse> getByStudent(@PathVariable("studentId") Long studentId) {
        return courseService.getByStudent(studentId);
    }

    @GetMapping("/{id}/students")
    public List<StudentResponse> getStudents(@PathVariable("id") Long id) {
        return courseService.getStudents(id);
    }

    @GetMapping("/{id}/students/count")
    public Integer getStudentCount(@PathVariable("id") Long id) {
        return courseService.getStudentCount(id);
    }

    @PutMapping("/{id}")
    public CourseResponse update(@PathVariable("id") Long id, @RequestBody CourseRequest request) {
        return courseService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        courseService.delete(id);
    }
}
