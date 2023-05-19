package MyException;

public class CurrencyDidNotExist extends Exception {
    public CurrencyDidNotExist() {
        super();
    }

    public CurrencyDidNotExist(String message) {
        super(message);
    }

    public CurrencyDidNotExist(String message, Throwable cause) {
        super(message, cause);
    }

    public CurrencyDidNotExist(Throwable cause) {
        super(cause);
    }

    protected CurrencyDidNotExist(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
