package org.clc.exception;

/**
 * @version 1.0
 * @description: TODO
 */
public class JwtAuthenticationException extends Exception{
    public JwtAuthenticationException() {
    }

    public JwtAuthenticationException(String msg) {
        super(msg);
    }
}
