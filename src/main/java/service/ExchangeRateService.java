package service;

import MyException.*;
import dao.daoImpl.CurrenciesDao;
import dao.daoImpl.ExchangeRatesDao;
import dto.ExchangeRatesDTO;
import entity.Currencies;
import entity.ExchangeRates;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import static service.ObjectToJson.getSimpleJson;

public class ExchangeRateService {

    private final static CurrenciesDao curDao = new CurrenciesDao();
    private final static ExchangeRatesDao exDAO = new ExchangeRatesDao();

    public static ExchangeRatesDTO getExchangeResult(String from, String to, String amount) throws CurrencyPairIsNotValid, RateOrAmountIsNotValid, SQLException, CurrencyDidNotExist, ExchangeRatesIsNotExistException {
        from = from.toUpperCase();
        to = to.toUpperCase();
        if (from.equals(to)) throw new CurrencyPairIsNotValid("Currency is not valid");

        if (isCurrencyValid(from) || isCurrencyValid(to)) throw new CurrencyPairIsNotValid("Currency is not valid");
        if (!isRateValid(amount)) throw new RateOrAmountIsNotValid("Rate or amount is not valid");

        double amountDouble = Double.parseDouble(amount);
        Currencies baseCur = curDao.getByCode(from);
        Currencies targetCur = curDao.getByCode(to);

        ExchangeRates exchangeRate;
        double convertedAmountRow;
        double rateRow;
        try {
            exchangeRate = exDAO.getByCode(baseCur, targetCur);
            rateRow = exchangeRate.getRate();
        } catch (ExchangeRatesIsNotExistException e) {
            try {
                exchangeRate = exDAO.getByCode(targetCur, baseCur);
                rateRow = 1 / exchangeRate.getRate();
            } catch (ExchangeRatesIsNotExistException ex) {
                Currencies currenciesUSD = curDao.getByCode("USD");
                try {
                    ExchangeRates baseRatesUSD = exDAO.getByCode(baseCur, currenciesUSD);
                    ExchangeRates targetRatesUSD = exDAO.getByCode(currenciesUSD, targetCur);
                    rateRow = baseRatesUSD.getRate() * targetRatesUSD.getRate();
                } catch (ExchangeRatesIsNotExistException exc) {
                    throw new ExchangeRatesIsNotExistException("Exchange rates not exist");
                }
            }
        }

        convertedAmountRow = amountDouble * rateRow;
        double convertedAmount = getDoubleFormat(convertedAmountRow, 2);
        double rate = getDoubleFormat(rateRow, 2);

        return new ExchangeRatesDTO(baseCur, targetCur, rate, amountDouble, convertedAmount);
    }

    private static double getDoubleFormat(double doubleValue, int newScale) {
        BigDecimal bigDecimal = new BigDecimal(doubleValue);
        return bigDecimal.setScale(newScale, RoundingMode.HALF_UP).doubleValue();
    }

    public static String[] getValidExchangePair(String pair) throws CurrencyPairIsNotValid {
        String message = "CurrencyPairIsNotValid";
        pair = pair.toUpperCase();
        if (pair.length() != 6) throw new CurrencyPairIsNotValid(message);

        String[] curPair = new String[2];
        curPair[0] = pair.substring(0, 3);
        curPair[1] = pair.substring(3);

        return curPair;
    }

    public static String getExchangeRatePair (String request) throws CurrencyPairIsNotValid, ExchangeRatesIsNotExistException, SQLException, CurrencyDidNotExist {
        String message = "CurrencyDoesNotExist";
        String[] curPair = getValidExchangePair(request);

        Currencies curBase = curDao.getByCode(curPair[0]);
        Currencies curTarget = curDao.getByCode(curPair[1]);
        if (curBase.getId() == 0 || curTarget.getId() == 0) throw new CurrencyPairIsNotValid(message);
        ExchangeRates exchangeRates = exDAO.getByCode(curBase, curTarget);

        if (exchangeRates.getId() == 0) throw new ExchangeRatesIsNotExistException("ExchangePair doesn't exist");
        return getSimpleJson(exchangeRates);
    }

    public static String getResponseAfterUpdate(String pair, String rate) throws CurrencyPairIsNotValid, SQLException, CurrencyDidNotExist, ExchangeRatesIsNotExistException {
        String[] curPair = getValidExchangePair(pair);
        if (!isRateValid(rate)) throw new CurrencyPairIsNotValid("Fill correct currency or rate to fields");
        double doubleRate = Double.parseDouble(rate);
        Currencies curBase = curDao.getByCode(curPair[0]);
        Currencies curTarget = curDao.getByCode(curPair[1]);

        ExchangeRates exchangeRates = exDAO.getByCode(curBase, curTarget);

        double rateResult = getDoubleFormat(doubleRate, 6);

        exchangeRates.setRate(rateResult);

        exDAO.update(exchangeRates);

        return getSimpleJson(exchangeRates);
    }

    public static String getResponseAfterAdd(String baseCurrency, String targetCurrency, String rate) throws CurrencyPairIsNotValid, CurrencyAlreadyExistsException, CurrencyDidNotExist, NumberFormatException, SQLException {
        baseCurrency = baseCurrency.toUpperCase();
        targetCurrency = targetCurrency.toUpperCase();

        if (validateCurrencyName(baseCurrency) && validateCurrencyName(targetCurrency) && isRateValid(rate)) {

            Currencies baseCur = curDao.getByCode(baseCurrency);
            if (baseCur.getId() == 0) throw new CurrencyDidNotExist("Currency didn't exist in table");

            Currencies targetCur = curDao.getByCode(targetCurrency);
            if (targetCur.getId() == 0) throw new CurrencyDidNotExist("Currency didn't exist in table");

            double doubleRate = Double.parseDouble(rate);
            double rateResult = getDoubleFormat(doubleRate, 6);

            ExchangeRates exchangeRates = new ExchangeRates(baseCur, targetCur, rateResult);
            int id = exDAO.add(exchangeRates);
            exchangeRates.setId(id);

            return getSimpleJson(exchangeRates);
        } else {
            throw new CurrencyPairIsNotValid("Fill correct currency or rate to fields");
        }
    }

    private static boolean validateCurrencyName(String currencyName) {
        return currencyName.matches("[A-Z]*") && currencyName.length() == 3;
    }

    private static boolean isRateValid(String rate) throws NumberFormatException {
        double rateDouble = Double.parseDouble(rate);
        return true;
    }

    private static boolean isCurrencyValid(String currency) {
        return currency.length() != 3 || !currency.matches("[A-Z]*");
    }
}
