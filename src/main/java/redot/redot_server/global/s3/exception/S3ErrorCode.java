package redot.redot_server.global.s3.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import redot.redot_server.global.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum S3ErrorCode implements ErrorCode {
    FILE_UPLOAD_FAILED(500, 5000, "파일 업로드에 실패했습니다."),
    FILE_DELETE_FAILED(500, 5001, "파일 삭제에 실패했습니다."),
    FILE_DELETE_UNKNOWN_ERROR(500, 5002, "파일 삭제 중 알 수 없는 오류가 발생했습니다.");

    private final int statusCode;
    private final int exceptionCode;
    private final String message;
}
