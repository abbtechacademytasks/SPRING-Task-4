package az.iktex.organization.structure.testders.response;

import java.math.BigDecimal;

public record EmployeeResponse(
        Long id,
        String name,
        String surname,
        BigDecimal salary,
        Long departmentId,
        String departmentName
) {
}
