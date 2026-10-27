package az.iktex.organization.structure.testders.request;

import az.iktex.organization.structure.testders.enums.Gender;

public record UserRequest(
        String name,
        String surname,
        String email,
        Gender gender
) {
}
