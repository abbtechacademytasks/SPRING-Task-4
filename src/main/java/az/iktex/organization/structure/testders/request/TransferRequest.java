package az.iktex.organization.structure.testders.request;

import java.math.BigDecimal;

public record TransferRequest(
        Long departmentId,
        BigDecimal raisePercent
) {
}