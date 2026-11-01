package az.iktex.organization.structure.testders.response;

import java.util.List;

public record BulkEnrollmentResponse(Long studentId, List<Long> enrolledCourseIds, List<Long> fullCourseIds) {
}
