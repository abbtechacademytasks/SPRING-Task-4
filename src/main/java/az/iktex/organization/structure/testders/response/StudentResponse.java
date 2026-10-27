package az.iktex.organization.structure.testders.response;

import java.util.Set;

public record StudentResponse(Long id, String name, String surname, Integer age, Set<CourseResponse> courses) {
}
