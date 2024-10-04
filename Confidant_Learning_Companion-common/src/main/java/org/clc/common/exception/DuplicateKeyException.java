package org.clc.common.exception;

/**
 * @version 1.0
 * @description: TODO
 */
public class DuplicateKeyException extends RuntimeException {
    public DuplicateKeyException() {
    }

    public DuplicateKeyException(String msg) {
        super(msg);
    }
}
