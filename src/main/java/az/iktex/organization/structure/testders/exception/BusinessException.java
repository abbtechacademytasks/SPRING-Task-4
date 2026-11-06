package az.iktex.organization.structure.testders.exception;

import az.iktex.organization.structure.testders.enums.ErrorCode;

public class BusinessException extends BaseException {

    public BusinessException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }
}
