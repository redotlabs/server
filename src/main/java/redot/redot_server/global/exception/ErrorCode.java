package redot.redot_server.global.exception;

public interface ErrorCode {
    int getStatusCode();
    int getExceptionCode();
    String getMessage();
}
