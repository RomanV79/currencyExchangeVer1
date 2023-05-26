package MyException;

public class NoIdReturnAfterAddException extends Exception {
    public NoIdReturnAfterAddException() {
        super();
    }

    public NoIdReturnAfterAddException(String message) {
        super(message);
    }

    public NoIdReturnAfterAddException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoIdReturnAfterAddException(Throwable cause) {
        super(cause);
    }

    protected NoIdReturnAfterAddException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
