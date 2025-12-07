package redot.redot_server.global.s3.exception;

import redot.redot_server.global.exception.BaseException;

public class ImageUploadException extends BaseException {
    public ImageUploadException(ImageErrorCode errorCode) {
        super(errorCode);
    }
}
