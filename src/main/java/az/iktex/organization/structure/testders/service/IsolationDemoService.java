package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.exception.NotFoundException;
import az.iktex.organization.structure.testders.exception.InvalidRequestException;
import az.iktex.organization.structure.testders.enums.ErrorCode;
import az.iktex.organization.structure.testders.exception.CourseFullException;
import az.iktex.organization.structure.testders.exception.BusinessException;
import az.iktex.organization.structure.testders.exception.AlreadyExistsException;
import az.iktex.organization.structure.testders.entity.Employee;
import az.iktex.organization.structure.testders.entity.Course;
import az.iktex.organization.structure.testders.entity.Student;
import az.iktex.organization.structure.testders.repository.CourseRepository;
import az.iktex.organization.structure.testders.repository.StudentRepository;
import az.iktex.organization.structure.testders.response.EnrollmentAttemptResponse;
import az.iktex.organization.structure.testders.repository.EmployeeRepository;
import az.iktex.organization.structure.testders.response.SalaryChangeResponse;
import az.iktex.organization.structure.testders.response.SalaryReportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class IsolationDemoService {

    private final EmployeeRepository employeeRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    @Transactional(
            readOnly = true,
            isolation = Isolation.READ_COMMITTED
    )
    public SalaryReportResponse averageReadCommitted(
            Long departmentId,
            long delayMs
    ) {
        if (delayMs < 0 || delayMs > 15000) {
            throw new InvalidRequestException(ErrorCode.INVALID_DELAY);
        }

        // Entity əvəzinə avg sorğusu istifadə olunur ki, L1 fərqi gizlətməsin.
        Double firstAvg =
                employeeRepository.findAverageSalaryByDepartmentId(departmentId);

        pause(delayMs);

        Double secondAvg =
                employeeRepository.findAverageSalaryByDepartmentId(departmentId);

        return new SalaryReportResponse(
                firstAvg,
                secondAvg,
                "READ_COMMITTED"
        );
    }

    @Transactional(
            readOnly = true,
            isolation = Isolation.REPEATABLE_READ
    )
    public SalaryReportResponse averageRepeatableRead(
            Long departmentId,
            long delayMs
    ) {
        if (delayMs < 0 || delayMs > 15000) {
            throw new InvalidRequestException(ErrorCode.INVALID_DELAY);
        }

        // Entity əvəzinə avg sorğusu istifadə olunur ki, L1 fərqi gizlətməsin.
        Double firstAvg =
                employeeRepository.findAverageSalaryByDepartmentId(departmentId);

        pause(delayMs);

        Double secondAvg =
                employeeRepository.findAverageSalaryByDepartmentId(departmentId);

        return new SalaryReportResponse(
                firstAvg,
                secondAvg,
                "REPEATABLE_READ"
        );
    }

    private void pause(long delayMs) {
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            BusinessException interrupted = new BusinessException(ErrorCode.OPERATION_INTERRUPTED);
            interrupted.initCause(exception);
            throw interrupted;
        }
    }

    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.READ_COMMITTED
    )
    public SalaryChangeResponse raiseReadCommitted(
            Long employeeId,
            BigDecimal amount,
            long delayMs
    ) {
        if (amount == null || amount.signum() <= 0) {
            throw new InvalidRequestException(ErrorCode.INVALID_RAISE_AMOUNT);
        }

        if (delayMs < 0 || delayMs > 15000) {
            throw new InvalidRequestException(ErrorCode.INVALID_DELAY);
        }

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new NotFoundException(ErrorCode.EMPLOYEE_NOT_FOUND, employeeId));

        BigDecimal oldSalary = employee.getSalary();

        pause(delayMs);

        BigDecimal newSalary = oldSalary.add(amount)
                .setScale(2, RoundingMode.HALF_UP);

        employee.setSalary(newSalary);

        return new SalaryChangeResponse(
                employeeId,
                oldSalary,
                newSalary,
                "READ_COMMITTED"
        );
    }

    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.REPEATABLE_READ
    )
    public SalaryChangeResponse raiseRepeatableRead(
            Long employeeId,
            BigDecimal amount,
            long delayMs
    ) {
        if (amount == null || amount.signum() <= 0) {
            throw new InvalidRequestException(ErrorCode.INVALID_RAISE_AMOUNT);
        }

        if (delayMs < 0 || delayMs > 15000) {
            throw new InvalidRequestException(ErrorCode.INVALID_DELAY);
        }

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new NotFoundException(ErrorCode.EMPLOYEE_NOT_FOUND, employeeId));

        BigDecimal oldSalary = employee.getSalary();

        pause(delayMs);

        BigDecimal newSalary = oldSalary.add(amount)
                .setScale(2, RoundingMode.HALF_UP);

        employee.setSalary(newSalary);

        return new SalaryChangeResponse(
                employeeId,
                oldSalary,
                newSalary,
                "REPEATABLE_READ"
        );
    }

    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.REPEATABLE_READ
    )
    public EnrollmentAttemptResponse enrollRepeatableRead(
            Long courseId,
            Long studentId,
            long delayMs
    ) {
        if (delayMs < 0 || delayMs > 15000) {
            throw new InvalidRequestException(ErrorCode.INVALID_DELAY);
        }

        Student student = studentRepository.findByIdWithCourses(studentId)
                .orElseThrow(() ->
                        new NotFoundException(ErrorCode.STUDENT_NOT_FOUND, studentId));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new NotFoundException(ErrorCode.COURSE_NOT_FOUND, courseId));

        if (studentRepository.existsByIdAndCoursesId(studentId, courseId)) {
            throw new AlreadyExistsException(ErrorCode.STUDENT_ALREADY_ENROLLED);
        }

        int countBefore =
                courseRepository.countStudentsByCourseId(courseId);

        if (countBefore >= course.getCapacity()) {
            throw new CourseFullException(courseId);
        }

        pause(delayMs);

        student.addCourse(course);

        return new EnrollmentAttemptResponse(
                courseId,
                studentId,
                countBefore,
                course.getCapacity(),
                "REPEATABLE_READ"
        );
    }

    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.SERIALIZABLE
    )
    public EnrollmentAttemptResponse enrollSerializable(
            Long courseId,
            Long studentId,
            long delayMs
    ) {
        if (delayMs < 0 || delayMs > 15000) {
            throw new InvalidRequestException(ErrorCode.INVALID_DELAY);
        }

        Student student = studentRepository.findByIdWithCourses(studentId)
                .orElseThrow(() ->
                        new NotFoundException(ErrorCode.STUDENT_NOT_FOUND, studentId));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new NotFoundException(ErrorCode.COURSE_NOT_FOUND, courseId));

        if (studentRepository.existsByIdAndCoursesId(studentId, courseId)) {
            throw new AlreadyExistsException(ErrorCode.STUDENT_ALREADY_ENROLLED);
        }

        int countBefore =
                courseRepository.countStudentsByCourseId(courseId);

        if (countBefore >= course.getCapacity()) {
            throw new CourseFullException(courseId);
        }

        pause(delayMs);

        student.addCourse(course);

        return new EnrollmentAttemptResponse(
                courseId,
                studentId,
                countBefore,
                course.getCapacity(),
                "SERIALIZABLE"
        );
    }
}
