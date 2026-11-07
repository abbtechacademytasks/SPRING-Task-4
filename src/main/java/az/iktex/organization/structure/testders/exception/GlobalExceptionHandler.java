package az.iktex.organization.structure.testders.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.context.MessageSource;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import az.iktex.organization.structure.testders.enums.ErrorCode;
import az.iktex.organization.structure.testders.response.ErrorResponse;
import org.slf4j.MDC;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBase(
            BaseException exception,
            HttpServletRequest request
    ) {
        return build(
                request,
                exception,
                exception.getErrorCode(),
                Map.of(),
                exception.getArgs()
        );
    }

    private ResponseEntity<ErrorResponse> build(
            HttpServletRequest request,
            Exception exception,
            ErrorCode errorCode,
            Map<String, String> errors,
            Object... args
    ) {
        logError(request, exception, errorCode, errors, args);
        String key = errorCode.getMessageKey();
        String message = messageSource.getMessage(
                key,
                args,
                key,
                LocaleContextHolder.getLocale()
        );

        ErrorResponse response = new ErrorResponse(
                errorCode.getStatus().value(),
                errorCode.getCode(),
                message,
                errors,
                MDC.get("traceId"),
                LocalDateTime.now()
        );

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    private void logError(
            HttpServletRequest request,
            Exception exception,
            ErrorCode errorCode,
            Map<String, String> errors,
            Object... args
    ) {
        String message = "%s %s -> %d %s (%s) [%s] args=%s fields=%s".formatted(
                request.getMethod(),
                request.getRequestURI(),
                errorCode.getStatus().value(),
                errorCode.getCode(),
                errorCode.getMessageKey(),
                exception.getClass().getSimpleName(),
                java.util.Arrays.toString(args),
                errors.keySet()
        );

        if (errorCode.getStatus().is5xxServerError()) {
            log.error(message, exception);
        } else {
            log.warn(message);
        }
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMalformedRequest(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {
        return build(
                request,
                exception,
                ErrorCode.MALFORMED_REQUEST,
                Map.of()
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request
    ) {
        return build(
                request,
                exception,
                ErrorCode.TYPE_MISMATCH,
                Map.of()
        );
    }
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(
            MissingServletRequestParameterException exception,
            HttpServletRequest request
    ) {
        return build(request, exception, ErrorCode.MISSING_PARAMETER, Map.of());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException exception,
            HttpServletRequest request
    ) {
        return build(request, exception, ErrorCode.METHOD_NOT_SUPPORTED, Map.of());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleUrlNotFound(
            NoResourceFoundException exception,
            HttpServletRequest request
    ) {
        return build(request, exception, ErrorCode.URL_NOT_FOUND, Map.of());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(
            DataIntegrityViolationException exception,
            HttpServletRequest request
    ) {
        return build(request, exception, ErrorCode.DATA_INTEGRITY_VIOLATION, Map.of());
    }

    @ExceptionHandler(ConcurrencyFailureException.class)
    public ResponseEntity<ErrorResponse> handleConcurrency(
            ConcurrencyFailureException exception,
            HttpServletRequest request
    ) {
        return build(request, exception, ErrorCode.CONCURRENT_OPERATION, Map.of());
    }

    @ExceptionHandler(IllegalTransactionStateException.class)
    public ResponseEntity<ErrorResponse> handleTransactionState(
            IllegalTransactionStateException exception,
            HttpServletRequest request
    ) {
        return build(request, exception, ErrorCode.TRANSACTION_STATE, Map.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(
            Exception exception,
            HttpServletRequest request
    ) {
        return build(request, exception, ErrorCode.INTERNAL_ERROR, Map.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        Map<String, String> errors = new LinkedHashMap<>();

        for (var fieldError : exception.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(
                    fieldError.getField(),
                    fieldError.getDefaultMessage()
            );
        }

        return build(
                request,
                exception,
                ErrorCode.VALIDATION_ERROR,
                errors
        );
    }
}
