package MyException;

public class CurrencyAlreadyExistsException extends Exception {

    public CurrencyAlreadyExistsException() {
    }

    public CurrencyAlreadyExistsException(String message) {
        super(message);
    }

    public CurrencyAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public CurrencyAlreadyExistsException(Throwable cause) {
        super(cause);
    }

    public CurrencyAlreadyExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
