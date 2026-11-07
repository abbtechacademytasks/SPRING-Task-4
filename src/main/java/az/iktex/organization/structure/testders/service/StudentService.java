package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.exception.BusinessException;
import az.iktex.organization.structure.testders.exception.AlreadyExistsException;
import az.iktex.organization.structure.testders.enums.ErrorCode;
import az.iktex.organization.structure.testders.exception.NotFoundException;
import az.iktex.organization.structure.testders.request.StudentRequest;
import az.iktex.organization.structure.testders.response.StudentResponse;
import az.iktex.organization.structure.testders.entity.Course;
import az.iktex.organization.structure.testders.entity.Student;
import az.iktex.organization.structure.testders.mapper.StudentMapper;
import az.iktex.organization.structure.testders.repository.CourseRepository;
import az.iktex.organization.structure.testders.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final StudentMapper studentMapper;
    private final EnrollmentService enrollmentService;

    @Transactional(propagation = Propagation.REQUIRED)
    public StudentResponse create(StudentRequest request) {
        Student student = studentMapper.toEntity(request);
        return studentMapper.toResponse(studentRepository.save(student));
    }

    public StudentResponse getById(Long id) {
        Student student = studentRepository.findByIdWithCourses(id)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.STUDENT_NOT_FOUND, id
                ));
        return studentMapper.toDetailResponse(student);
    }

    public List<StudentResponse> getAll() {
        return studentMapper.toResponseList(studentRepository.findAll());
    }

    public List<StudentResponse> getAllWithoutCourses() {
        return studentMapper.toResponseList(studentRepository.findAllWithoutCourses());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public StudentResponse update(Long id, StudentRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.STUDENT_NOT_FOUND, id));
        studentMapper.updateEntity(request, student);
        return studentMapper.toResponse(student);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.STUDENT_NOT_FOUND, id));
        studentRepository.delete(student);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void enroll(Long studentId, Long courseId) {
        Student student = studentRepository.findByIdWithCourses(studentId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.STUDENT_NOT_FOUND, studentId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.COURSE_NOT_FOUND, courseId));

        if (studentRepository.existsByIdAndCoursesId(studentId, courseId)) {
            throw new AlreadyExistsException(ErrorCode.STUDENT_ALREADY_ENROLLED);
        }

        enrollmentService.reserveSeat(courseId);
        student.addCourse(course);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void unenroll(Long studentId, Long courseId) {
        Student student = studentRepository.findByIdWithCourses(studentId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.STUDENT_NOT_FOUND, studentId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.COURSE_NOT_FOUND, courseId));

        if (!studentRepository.existsByIdAndCoursesId(studentId, courseId)) {
            throw new BusinessException(ErrorCode.STUDENT_NOT_ENROLLED);
        }

        student.removeCourse(course);
    }
}
