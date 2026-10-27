package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.request.CourseRequest;
import az.iktex.organization.structure.testders.response.CourseResponse;
import az.iktex.organization.structure.testders.response.StudentResponse;
import az.iktex.organization.structure.testders.entity.Course;
import az.iktex.organization.structure.testders.entity.Student;
import az.iktex.organization.structure.testders.mapper.CourseMapper;
import az.iktex.organization.structure.testders.mapper.StudentMapper;
import az.iktex.organization.structure.testders.repository.CourseRepository;
import az.iktex.organization.structure.testders.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final CourseMapper courseMapper;
    private final StudentMapper studentMapper;

    @Transactional
    public CourseResponse create(CourseRequest request) {
        if (courseRepository.existsByTitle(request.title())) {
            throw new IllegalStateException("Course title already exists");
        }
        Course course = courseMapper.toEntity(request);
        return courseMapper.toResponse(courseRepository.save(course));
    }

    public CourseResponse getById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found: " + id));
        return courseMapper.toResponse(course);
    }

    public List<CourseResponse> getAll() {
        return courseMapper.toResponseList(courseRepository.findAll());
    }

    public List<CourseResponse> getByStudent(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new EntityNotFoundException("Student not found: " + studentId);
        }
        return courseMapper.toResponseList(courseRepository.findAllByStudentId(studentId));
    }

    public List<StudentResponse> getStudents(Long id) {
        Course course = courseRepository.findByIdWithStudents(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found: " + id));
        return studentMapper.toResponseList(new ArrayList<>(course.getStudents()));
    }

    public Integer getStudentCount(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new EntityNotFoundException("Course not found: " + id);
        }
        return courseRepository.countStudentsByCourseId(id);
    }

    @Transactional
    public CourseResponse update(Long id, CourseRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found: " + id));
        if (courseRepository.existsByTitleAndIdNot(request.title(), id)) {
            throw new IllegalStateException("Course title already exists");
        }
        courseMapper.updateEntity(request, course);
        return courseMapper.toResponse(course);
    }

    @Transactional
    public void delete(Long id) {
        Course course = courseRepository.findByIdWithStudents(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Course not found: " + id));

        for (Student student : new HashSet<>(course.getStudents())) {
            student.removeCourse(course);
        }

        courseRepository.delete(course);
    }
}
