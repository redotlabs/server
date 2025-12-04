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

    public static ErrorResponse of(ErrorCode errorCode, String overrideMessage) {
        return new ErrorResponse(
                errorCode.getStatusCode(),
                errorCode.getExceptionCode(),
                overrideMessage == null || overrideMessage.isBlank()
                        ? errorCode.getMessage()
                        : overrideMessage
        );
    }
}
