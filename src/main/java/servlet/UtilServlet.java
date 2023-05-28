package servlet;

public class UtilServlet {

    protected boolean isCurrencyReqValid(String currency) {
        return currency.matches("[a-zA-Z]+") && currency.length() == 3;
    }

    protected boolean isRateValid(String rate) {
        return rate.matches("^\\d*\\.?\\d+$");
    }

    protected boolean isExchangePairValid (String pair) {
        return pair.matches("[a-zA-Z]+") && pair.length() == 6;
    }
}
