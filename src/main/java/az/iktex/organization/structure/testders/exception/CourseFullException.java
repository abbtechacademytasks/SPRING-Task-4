package az.iktex.organization.structure.testders.exception;

import az.iktex.organization.structure.testders.enums.ErrorCode;

public class CourseFullException extends BusinessException {
    public CourseFullException(Long courseId) {
        super(ErrorCode.COURSE_FULL, courseId);
    }
}
