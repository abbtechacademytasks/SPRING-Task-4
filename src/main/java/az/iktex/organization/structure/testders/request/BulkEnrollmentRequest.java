package az.iktex.organization.structure.testders.request;

import java.util.List;

public record BulkEnrollmentRequest(List<Long> courseIds) {
}
