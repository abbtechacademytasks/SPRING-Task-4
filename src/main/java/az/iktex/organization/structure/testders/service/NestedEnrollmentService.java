package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.exception.CourseFullException;
import az.iktex.organization.structure.testders.repository.JdbcEnrollmentRepository;
import jakarta.persistence.EntityNotFoundException;
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
                .orElseThrow(() -> new EntityNotFoundException("Course not found: " + courseId));
        if (repository.isEnrolled(studentId, courseId)) {
            throw new IllegalStateException("Student is already enrolled in this course");
        }

        // Savepoint INSERT-i geri qaytarır; burada managed JPA entity dəyişdirilmir.
        repository.enroll(studentId, courseId);
        if (repository.countStudents(courseId) > capacity) {
            throw new CourseFullException(courseId);
        }
    }
}
