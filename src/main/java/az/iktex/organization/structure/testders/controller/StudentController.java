package az.iktex.organization.structure.testders.controller;

import az.iktex.organization.structure.testders.request.StudentRequest;
import az.iktex.organization.structure.testders.response.StudentResponse;
import az.iktex.organization.structure.testders.service.StudentService;
import az.iktex.organization.structure.testders.service.BulkEnrollmentService;
import az.iktex.organization.structure.testders.request.BulkEnrollmentRequest;
import az.iktex.organization.structure.testders.response.BulkEnrollmentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Student", description = "Student management")
public class StudentController {

    private final StudentService studentService;
    private final BulkEnrollmentService bulkEnrollmentService;

    @Operation(
            summary = "Enroll in several courses",
            description = "Full courses are skipped. Missing student or course returns 404 (ERR-2000/ERR-2100), duplicate enrollment 409 (ERR-2001), invalid IDs 400 (ERR-2104)."
    )
    @PostMapping("/{id}/courses/bulk")
    public BulkEnrollmentResponse enrollBulk(@PathVariable("id") Long id, @Valid @RequestBody BulkEnrollmentRequest request) {
        return bulkEnrollmentService.enroll(id, request.courseIds());
    }

    @Operation(
            summary = "Create student",
            description = "Invalid request returns 400 (ERR-1000/ERR-1002)."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudentResponse create(@Valid @RequestBody StudentRequest request) {
        return studentService.create(request);
    }

    @Operation(
            summary = "Get student by ID",
            description = "Returns 404 (ERR-2000) if the student does not exist."
    )
    @GetMapping("/{id}")
    public StudentResponse getById(@PathVariable("id") Long id) {
        return studentService.getById(id);
    }

    @Operation(
            summary = "List students",
            description = "Returns all students."
    )
    @GetMapping
    public List<StudentResponse> getAll() {
        return studentService.getAll();
    }

    @Operation(
            summary = "List students without courses",
            description = "Returns students with no course links."
    )
    @GetMapping("/without-courses")
    public List<StudentResponse> getAllWithoutCourses() {
        return studentService.getAllWithoutCourses();
    }

    @Operation(
            summary = "Update student",
            description = "Missing student returns 404 (ERR-2000), invalid request 400 (ERR-1000/ERR-1002)."
    )
    @PutMapping("/{id}")
    public StudentResponse update(@PathVariable("id") Long id, @Valid @RequestBody StudentRequest request) {
        return studentService.update(id, request);
    }

    @Operation(
            summary = "Delete student",
            description = "Missing student returns 404 (ERR-2000)."
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        studentService.delete(id);
    }

    @Operation(
            summary = "Enroll student in course",
            description = "Missing student or course returns 404 (ERR-2000/ERR-2100), duplicate enrollment 409 (ERR-2001), full course 409 (ERR-2103)."
    )
    @PostMapping("/{studentId}/courses/{courseId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void enroll(@PathVariable("studentId") Long studentId, @PathVariable("courseId") Long courseId) {
        studentService.enroll(studentId, courseId);
    }

    @Operation(
            summary = "Unenroll student",
            description = "Missing student or course returns 404 (ERR-2000/ERR-2100), absent enrollment 409 (ERR-2002)."
    )
    @DeleteMapping("/{studentId}/courses/{courseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unenroll(@PathVariable("studentId") Long studentId, @PathVariable("courseId") Long courseId) {
        studentService.unenroll(studentId, courseId);
    }
}
