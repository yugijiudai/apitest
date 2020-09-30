package com.lml.core.exception;

/**
 * @author yugi
 * @apiNote 初始化异常
 * @since 2019-08-06
 */
public class InitException extends RuntimeException {


    public InitException(Throwable cause) {
        super(cause);
    }

    public InitException(String message) {
        super(message);
    }


    public InitException(String message, Throwable cause) {
        super(message, cause);
    }

}
