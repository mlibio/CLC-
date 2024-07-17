package org.clc.exception;

/**
 * 账号不存在异常
 */
public class UploadFailedException extends BaseException {

    public UploadFailedException() {
    }

    public UploadFailedException(String msg) {
        super(msg);
    }

}
