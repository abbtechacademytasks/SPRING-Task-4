package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.exception.NotFoundException;
import az.iktex.organization.structure.testders.enums.ErrorCode;
import az.iktex.organization.structure.testders.exception.AlreadyExistsException;
import az.iktex.organization.structure.testders.exception.CourseFullException;
import az.iktex.organization.structure.testders.repository.JdbcEnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NestedEnrollmentService {
    private final JdbcEnrollmentRepository repository;

    @Transactional(transactionManager = "nestedTransactionManager", propagation = Propagation.NESTED)
    public void enroll(Long studentId, Long courseId) {
        int capacity = repository.lockCourse(courseId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.COURSE_NOT_FOUND, courseId));
        if (repository.isEnrolled(studentId, courseId)) {
            throw new AlreadyExistsException(ErrorCode.STUDENT_ALREADY_ENROLLED);
        }

        // Savepoint INSERT-i geri qaytarır; burada managed JPA entity dəyişdirilmir.
        repository.enroll(studentId, courseId);
        if (repository.countStudents(courseId) > capacity) {
            throw new CourseFullException(courseId);
        }
    }
}
