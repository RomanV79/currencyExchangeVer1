package MyException;

public class ExchangeRatesIsNotExistException extends Exception {
    public ExchangeRatesIsNotExistException() {
        super();
    }

    public ExchangeRatesIsNotExistException(String message) {
        super(message);
    }

    public ExchangeRatesIsNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExchangeRatesIsNotExistException(Throwable cause) {
        super(cause);
    }

    protected ExchangeRatesIsNotExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
