package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.exception.NotFoundException;
import az.iktex.organization.structure.testders.exception.InvalidRequestException;
import az.iktex.organization.structure.testders.enums.ErrorCode;
import az.iktex.organization.structure.testders.exception.CourseFullException;
import az.iktex.organization.structure.testders.repository.JdbcEnrollmentRepository;
import az.iktex.organization.structure.testders.response.BulkEnrollmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BulkEnrollmentService {
    private final JdbcEnrollmentRepository repository;
    private final NestedEnrollmentService nestedEnrollmentService;

    @Transactional(transactionManager = "nestedTransactionManager", propagation = Propagation.REQUIRED)
    public BulkEnrollmentResponse enroll(Long studentId, List<Long> courseIds) {
        if (courseIds == null || courseIds.isEmpty() || courseIds.stream().anyMatch(id -> id == null || id <= 0)
                || new HashSet<>(courseIds).size() != courseIds.size()) {
            throw new InvalidRequestException(ErrorCode.INVALID_COURSE_IDS);
        }
        if (!repository.studentExists(studentId)) {
            throw new NotFoundException(ErrorCode.STUDENT_NOT_FOUND, studentId);
        }

        List<Long> enrolled = new ArrayList<>();
        List<Long> full = new ArrayList<>();
        // Eyni kursları fərqli sıra ilə göndərən bulk çağırışlar lock-ları eyni sıra ilə alır.
        for (Long courseId : courseIds.stream().sorted().toList()) {
            try {
                nestedEnrollmentService.enroll(studentId, courseId);
                enrolled.add(courseId);
            } catch (CourseFullException exception) {
                full.add(courseId);
            }
        }
        return new BulkEnrollmentResponse(studentId, List.copyOf(enrolled), List.copyOf(full));
    }
}
