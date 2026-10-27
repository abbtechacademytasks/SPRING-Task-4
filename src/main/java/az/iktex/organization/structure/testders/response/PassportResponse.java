package az.iktex.organization.structure.testders.response;

import java.time.LocalDate;

public record PassportResponse(
        Long id,
        String serialNumber,
        LocalDate issueDate,
        String country
) {
}
