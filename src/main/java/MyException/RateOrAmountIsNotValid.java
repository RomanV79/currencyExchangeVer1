package MyException;

import entity.ExchangeRates;

public class RateOrAmountIsNotValid extends Exception {
    public RateOrAmountIsNotValid() {
        super();
    }

    public RateOrAmountIsNotValid(String message) {
        super(message);
    }

    public RateOrAmountIsNotValid(String message, Throwable cause) {
        super(message, cause);
    }

    public RateOrAmountIsNotValid(Throwable cause) {
        super(cause);
    }

    protected RateOrAmountIsNotValid(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
