package redot.redot_server.global.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException exception) {
        ErrorResponse errorResponse = ErrorResponse.from(exception.getErrorCode());
        return ResponseEntity
                .status(errorResponse.statusCode())
                .body(errorResponse);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ErrorResponse> handleBindException(BindException exception) {
        return buildResponse(GlobalErrorCode.INVALID_INPUT_VALUE, formatBindingErrors(exception.getBindingResult()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException exception) {
        String details = exception.getConstraintViolations().stream()
                .map(this::formatConstraintViolation)
                .collect(Collectors.joining(", "));
        return buildResponse(GlobalErrorCode.INVALID_INPUT_VALUE, details);
    }

    @ExceptionHandler({MissingServletRequestParameterException.class, MissingServletRequestPartException.class})
    public ResponseEntity<ErrorResponse> handleMissingParameter(Exception exception) {
        return buildResponse(GlobalErrorCode.MISSING_REQUEST_PARAMETER, exception.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        Throwable cause = exception.getMostSpecificCause();
        log.warn("JSON 파싱 실패: {}", cause != null ? cause.getMessage() : exception.getMessage());
        String message = null;
        return buildResponse(GlobalErrorCode.MALFORMED_JSON_REQUEST, message);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
        String requiredType = exception.getRequiredType() != null ? exception.getRequiredType().getSimpleName() : "";
        String message = String.format("%s 값 '%s'을(를) %s 타입으로 변환할 수 없습니다.",
                exception.getName(), exception.getValue(), requiredType);
        return buildResponse(GlobalErrorCode.TYPE_MISMATCH, message);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException exception) {
        String supported = exception.getSupportedHttpMethods() == null ? "" : exception.getSupportedHttpMethods().toString();
        String message = String.format("%s 메서드는 지원하지 않습니다. 지원 목록: %s", exception.getMethod(), supported);
        return buildResponse(GlobalErrorCode.METHOD_NOT_ALLOWED, message);
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ErrorResponse> handleMultipartException(MultipartException exception) {
        return buildResponse(GlobalErrorCode.MULTIPART_ERROR, exception.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(Exception exception) {
        return buildResponse(GlobalErrorCode.RESOURCE_NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnhandledException(Exception exception) {
        log.error("Unhandled exception caught", exception);
        ErrorResponse errorResponse = new ErrorResponse(500, 9999, "서버 내부 오류가 발생했습니다.");
        return ResponseEntity
                .status(errorResponse.statusCode())
                .body(errorResponse);
    }

    private ResponseEntity<ErrorResponse> buildResponse(ErrorCode errorCode, String detailMessage) {
        ErrorResponse errorResponse = ErrorResponse.of(errorCode, composeMessage(errorCode, detailMessage));
        return ResponseEntity.status(errorResponse.statusCode()).body(errorResponse);
    }

    private String composeMessage(ErrorCode errorCode, String detailMessage) {
        if (detailMessage == null || detailMessage.isBlank()) {
            return errorCode.getMessage();
        }
        return "%s (%s)".formatted(errorCode.getMessage(), detailMessage);
    }

    private String formatBindingErrors(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining(", "));
    }

    private String formatFieldError(org.springframework.validation.FieldError fieldError) {
        return "%s: %s".formatted(fieldError.getField(), fieldError.getDefaultMessage());
    }

    private String formatConstraintViolation(ConstraintViolation<?> violation) {
        return "%s: %s".formatted(violation.getPropertyPath(), violation.getMessage());
    }
}
