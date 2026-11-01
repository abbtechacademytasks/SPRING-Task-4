package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.response.EnrollmentAttemptResponse;
import az.iktex.organization.structure.testders.response.SalaryChangeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class IsolationRetryService {
    private final IsolationDemoService isolationDemoService;

    public SalaryChangeResponse raise(Long employeeId, BigDecimal amount, long delayMs) {
        return retry("salary", () -> isolationDemoService.raiseRepeatableRead(employeeId, amount, delayMs));
    }

    public EnrollmentAttemptResponse enroll(Long courseId, Long studentId, long delayMs) {
        return retry("enrollment", () -> isolationDemoService.enrollSerializable(courseId, studentId, delayMs));
    }

    private <T> T retry(String operation, Supplier<T> action) {
        for (int attempt = 1; ; attempt++) {
            System.out.println("Retry " + operation + ": attempt=" + attempt);
            try {
                // Ayrı bean-in proxy-si commit/rollback-i bitirmədən bu çağırış geri qayıtmır.
                return action.get();
            } catch (ConcurrencyFailureException exception) {
                if (attempt == 3) {
                    throw exception;
                }
                System.out.println("Retry " + operation + ": conflict, starting a new transaction");
            }
        }
    }
}
