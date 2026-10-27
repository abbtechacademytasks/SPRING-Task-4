package az.iktex.organization.structure.testders.response;

import az.iktex.organization.structure.testders.enums.Gender;

public record UserResponse(
        Long id,
        String name,
        String surname,
        String email,
        Gender gender,
        PassportResponse passport
) {
}
