package redot.redot_server.global.s3.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import redot.redot_server.global.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum ImageErrorCode implements ErrorCode {
    IMAGE_FILE_REQUIRED(400, 5200, "업로드할 이미지를 선택해주세요."),
    INVALID_IMAGE_FILE_NAME(400, 5201, "이미지 파일명이 올바르지 않습니다."),
    INVALID_IMAGE_EXTENSION(400, 5202, "지원하지 않는 이미지 확장자입니다."),
    INVALID_IMAGE_OWNER(400, 5203, "이미지를 저장할 대상을 찾을 수 없습니다."),
    IMAGE_TOO_LARGE(400, 5204, "허용된 크기를 초과한 이미지입니다."),
    UNSUPPORTED_IMAGE_TYPE(400, 5205, "지원하지 않는 이미지 형식입니다.");

    private final int statusCode;
    private final int exceptionCode;
    private final String message;
}
