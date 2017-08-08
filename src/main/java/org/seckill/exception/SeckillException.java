package org.seckill.exception;

/**
 * Created by wuchaoxiang on 2017/8/5
 */
public class SeckillException extends RuntimeException {

    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
