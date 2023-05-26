package MyException;

public class ServiceDidntAnswerException extends Exception {
    public ServiceDidntAnswerException() {
        super();
    }

    public ServiceDidntAnswerException(String message) {
        super(message);
    }

    public ServiceDidntAnswerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceDidntAnswerException(Throwable cause) {
        super(cause);
    }

    protected ServiceDidntAnswerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
