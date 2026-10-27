package az.iktex.organization.structure.testders.request;

import java.math.BigDecimal;

public record EmployeeRequest(String name, String surname, BigDecimal salary, Long departmentId) {
}
