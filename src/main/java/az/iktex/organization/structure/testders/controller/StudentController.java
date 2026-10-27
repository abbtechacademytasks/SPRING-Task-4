package az.iktex.organization.structure.testders.controller;

import az.iktex.organization.structure.testders.request.StudentRequest;
import az.iktex.organization.structure.testders.response.StudentResponse;
import az.iktex.organization.structure.testders.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudentResponse create(@RequestBody StudentRequest request) {
        return studentService.create(request);
    }

    @GetMapping("/{id}")
    public StudentResponse getById(@PathVariable("id") Long id) {
        return studentService.getById(id);
    }

    @GetMapping
    public List<StudentResponse> getAll() {
        return studentService.getAll();
    }

    @GetMapping("/without-courses")
    public List<StudentResponse> getAllWithoutCourses() {
        return studentService.getAllWithoutCourses();
    }

    @PutMapping("/{id}")
    public StudentResponse update(@PathVariable("id") Long id, @RequestBody StudentRequest request) {
        return studentService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        studentService.delete(id);
    }

    @PostMapping("/{studentId}/courses/{courseId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void enroll(@PathVariable("studentId") Long studentId, @PathVariable("courseId") Long courseId) {
        studentService.enroll(studentId, courseId);
    }

    @DeleteMapping("/{studentId}/courses/{courseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unenroll(@PathVariable("studentId") Long studentId, @PathVariable("courseId") Long courseId) {
        studentService.unenroll(studentId, courseId);
    }
}
