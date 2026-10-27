package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.request.StudentRequest;
import az.iktex.organization.structure.testders.response.StudentResponse;
import az.iktex.organization.structure.testders.entity.Course;
import az.iktex.organization.structure.testders.entity.Student;
import az.iktex.organization.structure.testders.mapper.StudentMapper;
import az.iktex.organization.structure.testders.repository.CourseRepository;
import az.iktex.organization.structure.testders.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final StudentMapper studentMapper;

    @Transactional
    public StudentResponse create(StudentRequest request) {
        Student student = studentMapper.toEntity(request);
        return studentMapper.toResponse(studentRepository.save(student));
    }

    public StudentResponse getById(Long id) {
        Student student = studentRepository.findByIdWithCourses(id)
                .orElseThrow(() -> new EntityNotFoundException("Student not found: " + id));
        return studentMapper.toDetailResponse(student);
    }

    public List<StudentResponse> getAll() {
        return studentMapper.toResponseList(studentRepository.findAll());
    }

    public List<StudentResponse> getAllWithoutCourses() {
        return studentMapper.toResponseList(studentRepository.findAllWithoutCourses());
    }

    @Transactional
    public StudentResponse update(Long id, StudentRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student not found: " + id));
        studentMapper.updateEntity(request, student);
        return studentMapper.toResponse(student);
    }

    @Transactional
    public void delete(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student not found: " + id));
        studentRepository.delete(student);
    }

    @Transactional
    public void enroll(Long studentId, Long courseId) {
        Student student = studentRepository.findByIdWithCourses(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found: " + studentId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found: " + courseId));

        if (studentRepository.existsByIdAndCoursesId(studentId, courseId)) {
            throw new IllegalStateException("Student is already enrolled in this course");
        }

        student.addCourse(course);
    }

    @Transactional
    public void unenroll(Long studentId, Long courseId) {
        Student student = studentRepository.findByIdWithCourses(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found: " + studentId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found: " + courseId));

        if (!studentRepository.existsByIdAndCoursesId(studentId, courseId)) {
            throw new IllegalStateException("Student is not enrolled in this course");
        }

        student.removeCourse(course);
    }
}
