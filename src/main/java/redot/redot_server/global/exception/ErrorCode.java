package redot.redot_server.global.exception;

public interface ErrorCode {
    int getStatus();
    String getError();
    String getMessage();
}
