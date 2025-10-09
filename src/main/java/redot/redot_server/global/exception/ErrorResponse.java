package redot.redot_server.global.exception;

public record ErrorResponse(
        int statusCode,
        int exceptionCode,
        String message
) {
    public static ErrorResponse from(ErrorCode errorCode) {
        return new ErrorResponse(
                errorCode.getStatusCode(),
                errorCode.getExceptionCode(),
                errorCode.getMessage()
        );
    }
}
