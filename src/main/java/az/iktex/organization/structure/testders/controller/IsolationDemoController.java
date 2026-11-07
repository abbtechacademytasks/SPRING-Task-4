package az.iktex.organization.structure.testders.controller;

import az.iktex.organization.structure.testders.exception.InvalidRequestException;
import az.iktex.organization.structure.testders.enums.ErrorCode;
import az.iktex.organization.structure.testders.response.EnrollmentAttemptResponse;
import az.iktex.organization.structure.testders.response.SalaryChangeResponse;
import az.iktex.organization.structure.testders.response.SalaryReportResponse;
import az.iktex.organization.structure.testders.service.IsolationDemoService;
import az.iktex.organization.structure.testders.service.IsolationRetryService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/isolation")
@RequiredArgsConstructor
public class IsolationDemoController {

    private final IsolationDemoService isolationDemoService;
    private final IsolationRetryService isolationRetryService;

    @GetMapping("/departments/{id}/salary-report")
    public SalaryReportResponse salaryReport(
            @PathVariable("id") Long departmentId,
            @RequestParam(name = "isolation",
                    defaultValue = "READ_COMMITTED") Isolation isolation,
            @RequestParam(name = "delayMs",
                    defaultValue = "5000") long delayMs
    ) {
        return switch (isolation) {
            case READ_COMMITTED ->
                    isolationDemoService.averageReadCommitted(
                            departmentId, delayMs
                    );
            case REPEATABLE_READ ->
                    isolationDemoService.averageRepeatableRead(
                            departmentId, delayMs
                    );
            default -> throw new InvalidRequestException(ErrorCode.INVALID_ISOLATION);
        };
    }

    @PostMapping("/employees/{id}/raise")
    public SalaryChangeResponse raise(
            @PathVariable("id") Long employeeId,
            @RequestParam("amount") BigDecimal amount,
            @RequestParam(name = "isolation",
                    defaultValue = "READ_COMMITTED") Isolation isolation,
            @RequestParam(name = "retry", defaultValue = "false") boolean retry,
            @RequestParam(name = "delayMs",
                    defaultValue = "5000") long delayMs
    ) {
        if (retry) {
            if (isolation != Isolation.REPEATABLE_READ) {
                throw new InvalidRequestException(ErrorCode.SALARY_RETRY_ISOLATION);
            }
            return isolationRetryService.raise(employeeId, amount, delayMs);
        }
        return switch (isolation) {
            case READ_COMMITTED ->
                    isolationDemoService.raiseReadCommitted(
                            employeeId, amount, delayMs
                    );
            case REPEATABLE_READ ->
                    isolationDemoService.raiseRepeatableRead(
                            employeeId, amount, delayMs
                    );
            default -> throw new InvalidRequestException(ErrorCode.INVALID_ISOLATION);
        };
    }

    @PostMapping("/courses/{courseId}/enroll/{studentId}")
    public EnrollmentAttemptResponse enroll(
            @PathVariable("courseId") Long courseId,
            @PathVariable("studentId") Long studentId,
            @RequestParam(name = "isolation",
                    defaultValue = "REPEATABLE_READ") Isolation isolation,
            @RequestParam(name = "retry", defaultValue = "false") boolean retry,
            @RequestParam(name = "delayMs",
                    defaultValue = "5000") long delayMs
    ) {
        if (retry) {
            if (isolation != Isolation.SERIALIZABLE) {
                throw new InvalidRequestException(ErrorCode.ENROLLMENT_RETRY_ISOLATION);
            }
            return isolationRetryService.enroll(courseId, studentId, delayMs);
        }
        return switch (isolation) {
            case REPEATABLE_READ ->
                    isolationDemoService.enrollRepeatableRead(
                            courseId, studentId, delayMs
                    );
            case SERIALIZABLE ->
                    isolationDemoService.enrollSerializable(
                            courseId, studentId, delayMs
                    );
            default -> throw new InvalidRequestException(ErrorCode.INVALID_ISOLATION);
        };
    }
}
