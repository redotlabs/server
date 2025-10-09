package redot.redot_server.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnhandledException(Exception exception) {
        log.error("Unhandled exception caught", exception);
        ErrorResponse errorResponse = new ErrorResponse(500, 9999, "서버 내부 오류가 발생했습니다.");
        return ResponseEntity
                .status(errorResponse.statusCode())
                .body(errorResponse);
    }
}
