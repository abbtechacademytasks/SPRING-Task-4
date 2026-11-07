package az.iktex.organization.structure.testders.exception;

import az.iktex.organization.structure.testders.enums.ErrorCode;

public class AlreadyExistsException extends BaseException {

    public AlreadyExistsException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }
}
