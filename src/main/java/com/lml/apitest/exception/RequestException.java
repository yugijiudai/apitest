package com.lml.apitest.exception;

/**
 * @author yugi
 * @apiNote 请求异常
 * @since 2019-09-23
 */
public class RequestException extends RuntimeException {


    public RequestException(Throwable cause) {
        super(cause);
    }

    public RequestException(String message) {
        super(message);
    }


    public RequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
