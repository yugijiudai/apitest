package com.lml.core.exception;

/**
 * @author yugi
 * @apiNote 业务异常
 * @since 2019-08-06
 */
public class BizException extends RuntimeException {


    public BizException(Throwable cause) {
        super(cause);
    }

    public BizException(String message) {
        super(message);
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }

}
