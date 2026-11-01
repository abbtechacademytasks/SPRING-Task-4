package az.iktex.organization.structure.testders.response;

import java.math.BigDecimal;

public record SalaryChangeResponse(
        Long employeeId,
        BigDecimal oldSalary,
        BigDecimal newSalary,
        String isolation
) {
}