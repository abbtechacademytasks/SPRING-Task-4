package az.iktex.organization.structure.testders.enums;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    VALIDATION_ERROR("ERR-1000", "error.validation", HttpStatus.BAD_REQUEST),
    INTERNAL_ERROR("ERR-1001", "error.internal", HttpStatus.INTERNAL_SERVER_ERROR),
    MALFORMED_REQUEST("ERR-1002", "error.request.malformed", HttpStatus.BAD_REQUEST),
    TYPE_MISMATCH("ERR-1003", "error.request.typeMismatch", HttpStatus.BAD_REQUEST),
    MISSING_PARAMETER("ERR-1004", "error.request.missingParam", HttpStatus.BAD_REQUEST),
    METHOD_NOT_SUPPORTED("ERR-1005", "error.request.methodNotSupported", HttpStatus.METHOD_NOT_ALLOWED),
    URL_NOT_FOUND("ERR-1006", "error.request.urlNotFound", HttpStatus.NOT_FOUND),
    DATA_INTEGRITY_VIOLATION("ERR-1007", "error.data.integrity", HttpStatus.CONFLICT),

    STUDENT_NOT_FOUND("ERR-2000", "error.student.notFound", HttpStatus.NOT_FOUND),
    STUDENT_ALREADY_ENROLLED("ERR-2001", "error.student.alreadyEnrolled", HttpStatus.CONFLICT),
    STUDENT_NOT_ENROLLED("ERR-2002", "error.student.notEnrolled", HttpStatus.CONFLICT),

    COURSE_NOT_FOUND("ERR-2100", "error.course.notFound", HttpStatus.NOT_FOUND),
    COURSE_ALREADY_EXISTS("ERR-2101", "error.course.alreadyExists", HttpStatus.CONFLICT),
    COURSE_TITLE_USED("ERR-2102", "error.course.titleUsed", HttpStatus.CONFLICT),

    DEPARTMENT_NOT_FOUND("ERR-2200", "error.department.notFound", HttpStatus.NOT_FOUND),
    DEPARTMENT_ALREADY_EXISTS("ERR-2201", "error.department.alreadyExists", HttpStatus.CONFLICT),
    DEPARTMENT_NAME_USED("ERR-2202", "error.department.nameUsed", HttpStatus.CONFLICT),
    DEPARTMENT_HAS_EMPLOYEES("ERR-2203", "error.department.hasEmployees", HttpStatus.CONFLICT),

    EMPLOYEE_NOT_FOUND("ERR-2300", "error.employee.notFound", HttpStatus.NOT_FOUND),
    EMPLOYEE_ALREADY_EXISTS("ERR-2301", "error.employee.alreadyExists", HttpStatus.CONFLICT),
    EMPLOYEE_SALARY_RANGE("ERR-2302", "error.employee.salaryRange", HttpStatus.BAD_REQUEST),

    AUTHOR_NOT_FOUND("ERR-2400", "error.author.notFound", HttpStatus.NOT_FOUND),
    AUTHOR_ALREADY_EXISTS("ERR-2401", "error.author.alreadyExists", HttpStatus.CONFLICT),

    BOOK_NOT_FOUND("ERR-2500", "error.book.notFound", HttpStatus.NOT_FOUND),
    BOOK_ALREADY_EXISTS("ERR-2501", "error.book.alreadyExists", HttpStatus.CONFLICT),
    BOOK_AUTHOR_ALREADY_LINKED("ERR-2502", "error.book.authorAlreadyLinked", HttpStatus.CONFLICT),
    BOOK_AUTHOR_NOT_LINKED("ERR-2503", "error.book.authorNotLinked", HttpStatus.NOT_FOUND),

    USER_NOT_FOUND("ERR-2600", "error.user.notFound", HttpStatus.NOT_FOUND),
    USER_EMAIL_EXISTS("ERR-2601", "error.user.emailExists", HttpStatus.CONFLICT),
    USER_EMAIL_USED("ERR-2602", "error.user.emailUsed", HttpStatus.CONFLICT),
    USER_PASSPORT_EXISTS("ERR-2603", "error.user.passportExists", HttpStatus.CONFLICT),
    PASSPORT_NOT_FOUND("ERR-2604", "error.passport.notFound", HttpStatus.NOT_FOUND),
    PASSPORT_SERIAL_EXISTS("ERR-2605", "error.passport.serialExists", HttpStatus.CONFLICT),

    CONCURRENT_OPERATION("ERR-1008", "error.transaction.concurrent", HttpStatus.CONFLICT),
    TRANSACTION_STATE("ERR-1009", "error.transaction.state", HttpStatus.CONFLICT),
    INVALID_DELAY("ERR-1010", "error.request.delay", HttpStatus.BAD_REQUEST),
    INVALID_ISOLATION("ERR-1011", "error.request.isolation", HttpStatus.BAD_REQUEST),
    UNKNOWN_CACHE_REGION("ERR-1012", "error.cache.region", HttpStatus.BAD_REQUEST),
    OPERATION_INTERRUPTED("ERR-1013", "error.operation.interrupted", HttpStatus.CONFLICT),
    SALARY_RETRY_ISOLATION("ERR-1014", "error.salary.retryIsolation", HttpStatus.BAD_REQUEST),
    ENROLLMENT_RETRY_ISOLATION("ERR-1015", "error.enrollment.retryIsolation", HttpStatus.BAD_REQUEST),
    COURSE_FULL("ERR-2103", "error.course.full", HttpStatus.CONFLICT),
    INVALID_COURSE_IDS("ERR-2104", "error.course.invalidIds", HttpStatus.BAD_REQUEST),
    EMPLOYEE_SALARY_LIMIT("ERR-2303", "error.employee.salaryLimit", HttpStatus.CONFLICT),
    INVALID_RAISE_PERCENT("ERR-2304", "error.employee.raisePercent", HttpStatus.BAD_REQUEST),
    INVALID_RAISE_AMOUNT("ERR-2305", "error.employee.raiseAmount", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String messageKey;
    private final HttpStatus status;

    ErrorCode(String code, String messageKey, HttpStatus status) {
        this.code = code;
        this.messageKey = messageKey;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public HttpStatus getStatus() { return status; }
}
