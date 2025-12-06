package redot.redot_server.global.s3.exception;

import redot.redot_server.global.exception.BaseException;

public class S3StorageException extends BaseException {
    public S3StorageException(S3ErrorCode errorCode) {
        super(errorCode);
    }

    public S3StorageException(S3ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
