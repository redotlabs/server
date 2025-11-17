package redot.redot_server.support.s3.exception;

import redot.redot_server.support.exception.BaseException;

public class S3Exception extends BaseException {
    public S3Exception(S3ErrorCode errorCode) {
        super(errorCode);
    }
}
