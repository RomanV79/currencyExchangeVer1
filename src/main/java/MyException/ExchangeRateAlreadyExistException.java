package MyException;

public class ExchangeRateAlreadyExistException extends Exception {

    public ExchangeRateAlreadyExistException() {
        super();
    }

    public ExchangeRateAlreadyExistException(String message) {
        super(message);
    }

    public ExchangeRateAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExchangeRateAlreadyExistException(Throwable cause) {
        super(cause);
    }

    protected ExchangeRateAlreadyExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
