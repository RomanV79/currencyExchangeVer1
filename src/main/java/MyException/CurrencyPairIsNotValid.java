package MyException;

public class CurrencyPairIsNotValid extends Exception {

    public CurrencyPairIsNotValid() {
        super();
    }

    public CurrencyPairIsNotValid(String message) {
        super(message);
    }

    public CurrencyPairIsNotValid(String message, Throwable cause) {
        super(message, cause);
    }

    public CurrencyPairIsNotValid(Throwable cause) {
        super(cause);
    }

    protected CurrencyPairIsNotValid(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
