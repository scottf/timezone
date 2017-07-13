package com.arondight.timezone;

public class TzException extends Exception {
    public TzException() {
    }

    public TzException(String message) {
        super(message);
    }

    public TzException(String message, Throwable cause) {
        super(message, cause);
    }

    public TzException(Throwable cause) {
        super(cause);
    }
}
