package az.iktex.organization.structure.testders.response;

public record EnrollmentAttemptResponse(
        Long courseId,
        Long studentId,
        int countBefore,
        int capacity,
        String isolation
) {
}
