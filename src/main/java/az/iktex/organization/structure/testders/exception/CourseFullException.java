package az.iktex.organization.structure.testders.exception;

public class CourseFullException extends IllegalStateException {
    public CourseFullException(Long courseId) {
        super("Course is full: " + courseId);
    }
}
