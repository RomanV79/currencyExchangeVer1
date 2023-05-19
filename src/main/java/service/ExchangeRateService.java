package service;

import MyException.CurrencyAlreadyExistsException;
import MyException.CurrencyDidNotExist;
import MyException.CurrencyPairIsNotValid;
import MyException.ExchangeRatesIsNotExistException;
import com.google.gson.Gson;
import dao.daoImpl.CurrenciesDaoImpl;
import dao.daoImpl.ExchangeRatesDaoImpl;
import entity.Currencies;
import entity.ExchangeRates;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Formatter;

import static service.ObjectToJson.getSimpleJson;

public class ExchangeRateService {
    public static String[] getValidExchangePair(String pair) throws CurrencyPairIsNotValid {
        String message = "CurrencyPairIsNotValid";
        pair = pair.toUpperCase();
        if (pair.length() != 6) throw new CurrencyPairIsNotValid(message);

        String[] curPair = new String[2];
        curPair[0] = pair.substring(0, 3);
        curPair[1] = pair.substring(3);

        return curPair;
    }

    public static String getExchangeRatePair (String request) throws CurrencyPairIsNotValid, ExchangeRatesIsNotExistException, SQLException {
        String message = "CurrencyDoesNotExist";
        String[] curPair = getValidExchangePair(request);

        CurrenciesDaoImpl cdi = new CurrenciesDaoImpl();
        ExchangeRatesDaoImpl erdi = new ExchangeRatesDaoImpl();
        Currencies curBase = cdi.getByCode(curPair[0]);
        Currencies curTarget = cdi.getByCode(curPair[1]);
        if (curBase.getId() == 0 || curTarget.getId() == 0) throw new CurrencyPairIsNotValid(message);
        ExchangeRates exchangeRates = erdi.getExchangeRateByCurPair(curBase, curTarget);

        if (exchangeRates.getId() == 0) throw new ExchangeRatesIsNotExistException("ExchangePair doesn't exist");
        return getSimpleJson(exchangeRates);
    }

    public static String getResponseAfterAdd(String baseCurrency, String targetCurrency, String rate) throws CurrencyPairIsNotValid, CurrencyAlreadyExistsException, CurrencyDidNotExist, NumberFormatException, SQLException {
        baseCurrency = baseCurrency.toUpperCase();
        targetCurrency = targetCurrency.toUpperCase();

        if (validateCurrencyName(baseCurrency) && validateCurrencyName(targetCurrency) && validateRate(rate)) {
            CurrenciesDaoImpl cdi = new CurrenciesDaoImpl();

            Currencies baseCur = cdi.getByCode(baseCurrency);
            if (baseCur.getId() == 0) throw new CurrencyDidNotExist("Currency didn't exist in table");

            Currencies targetCur = cdi.getByCode(targetCurrency);
            if (targetCur.getId() == 0) throw new CurrencyDidNotExist("Currency didn't exist in table");

            BigDecimal value = new BigDecimal(rate);
            double rateResult = value.setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();

            ExchangeRates exchangeRates = new ExchangeRates(baseCur, targetCur, rateResult);
            ExchangeRatesDaoImpl erdi = new ExchangeRatesDaoImpl();
            int id = erdi.add(exchangeRates);
            exchangeRates.setId(id);

            return getSimpleJson(exchangeRates);
        } else {
            throw new CurrencyPairIsNotValid("Fill correct currency or rate to fields");
        }
    }

    private static boolean validateCurrencyName(String currencyName) {
        return currencyName.matches("[A-Z]*") && currencyName.length() == 3;
    }

    private static boolean validateRate (String rate) throws NumberFormatException {
        double rateDouble = Double.parseDouble(rate);
        return true;
    }
}
