package com.lml.apitest.exception;

/**
 * @author yugi
 * @apiNote 数据访问异常
 * @since 2019-08-06
 */
public class DaoException extends RuntimeException {


    public DaoException(Throwable cause) {
        super(cause);
    }

    public DaoException(String message) {
        super(message);
    }

    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }

}
