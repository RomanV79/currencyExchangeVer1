package service;

import MyException.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dao.daoImpl.CurrenciesDaoImpl;
import dao.daoImpl.ExchangeRatesDaoImpl;
import entity.Currencies;
import entity.ExchangeRates;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import static service.ObjectToJson.getSimpleJson;

public class ExchangeRateService {

    public static String getExchangeResult(String from, String to, String amount) throws CurrencyPairIsNotValid, RateOrAmountIsNotValid, SQLException, CurrencyDidNotExist, ExchangeRatesIsNotExistException {
        from = from.toUpperCase();
        to = to.toUpperCase();
        if (from.equals(to)) throw new CurrencyPairIsNotValid("Currency is not valid");

        if (!isValidCurrency(from) || !isValidCurrency(to)) throw new CurrencyPairIsNotValid("Currency is not valid");
        if (!isValidRate(amount)) throw new RateOrAmountIsNotValid("Rate or amount is not valid");

        double amountDouble = Double.parseDouble(amount);

        CurrenciesDaoImpl cdi = new CurrenciesDaoImpl();
        Currencies baseCur = cdi.getByCode(from);
        Currencies targetCur = cdi.getByCode(to);

        ExchangeRatesDaoImpl erdi = new ExchangeRatesDaoImpl();
        ExchangeRates exchangeRate;
        double convertedAmountRow;
        double rateRow;
        try {
            exchangeRate = erdi.getExchangeRateByCurPair(baseCur, targetCur);
            rateRow = exchangeRate.getRate();
        } catch (ExchangeRatesIsNotExistException e) {
            try {
                exchangeRate = erdi.getExchangeRateByCurPair(targetCur, baseCur);
                rateRow = 1 / exchangeRate.getRate();
            } catch (ExchangeRatesIsNotExistException ex) {
                Currencies currenciesUSD = cdi.getByCode("USD");
                try {
                    ExchangeRates baseRatesUSD = erdi.getExchangeRateByCurPair(baseCur, currenciesUSD);
                    ExchangeRates targetRatesUSD = erdi.getExchangeRateByCurPair(currenciesUSD, targetCur);
                    rateRow = baseRatesUSD.getRate() * targetRatesUSD.getRate();
                } catch (ExchangeRatesIsNotExistException exc) {
                    throw new ExchangeRatesIsNotExistException("Exchange rates not exist");
                }
            }
        }

        convertedAmountRow = amountDouble * rateRow;
        BigDecimal valueAmount = new BigDecimal(convertedAmountRow);
        double convertedAmount = valueAmount.setScale(2, RoundingMode.HALF_UP).doubleValue();

        BigDecimal rateFormat = new BigDecimal(rateRow);
        double rate = rateFormat.setScale(2, RoundingMode.HALF_UP).doubleValue();

        ExchangeRates exRateOriginal = new ExchangeRates(baseCur, targetCur, rate);
        String json = getSimpleJson(exRateOriginal);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        jsonObject.addProperty("amount", amountDouble);
        jsonObject.addProperty("convertedAmount", convertedAmount);

        String result = gson.toJson(jsonObject).replace("\"id\": 0,\n", "");

        return result;
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

        CurrenciesDaoImpl cdi = new CurrenciesDaoImpl();
        ExchangeRatesDaoImpl erdi = new ExchangeRatesDaoImpl();
        Currencies curBase = cdi.getByCode(curPair[0]);
        Currencies curTarget = cdi.getByCode(curPair[1]);
        if (curBase.getId() == 0 || curTarget.getId() == 0) throw new CurrencyPairIsNotValid(message);
        ExchangeRates exchangeRates = erdi.getExchangeRateByCurPair(curBase, curTarget);

        if (exchangeRates.getId() == 0) throw new ExchangeRatesIsNotExistException("ExchangePair doesn't exist");
        return getSimpleJson(exchangeRates);
    }

    public static String getResponseAfterUpdate(String pair, String rate) throws CurrencyPairIsNotValid, SQLException, CurrencyDidNotExist, ExchangeRatesIsNotExistException {
        String[] curPair = getValidExchangePair(pair);
        if (!isValidRate(rate)) throw new CurrencyPairIsNotValid("Fill correct currency or rate to fields");
        CurrenciesDaoImpl cdi = new CurrenciesDaoImpl();
        Currencies curBase = cdi.getByCode(curPair[0]);
        Currencies curTarget = cdi.getByCode(curPair[1]);

        ExchangeRatesDaoImpl erdi = new ExchangeRatesDaoImpl();
        ExchangeRates exchangeRates = erdi.getExchangeRateByCurPair(curBase, curTarget);

        BigDecimal value = new BigDecimal(rate);
        double rateResult = value.setScale(6, RoundingMode.HALF_UP).doubleValue();

        exchangeRates.setRate(rateResult);

        erdi.update(exchangeRates);

        return getSimpleJson(exchangeRates);
    }

    public static String getResponseAfterAdd(String baseCurrency, String targetCurrency, String rate) throws CurrencyPairIsNotValid, CurrencyAlreadyExistsException, CurrencyDidNotExist, NumberFormatException, SQLException {
        baseCurrency = baseCurrency.toUpperCase();
        targetCurrency = targetCurrency.toUpperCase();

        if (validateCurrencyName(baseCurrency) && validateCurrencyName(targetCurrency) && isValidRate(rate)) {
            CurrenciesDaoImpl cdi = new CurrenciesDaoImpl();

            Currencies baseCur = cdi.getByCode(baseCurrency);
            if (baseCur.getId() == 0) throw new CurrencyDidNotExist("Currency didn't exist in table");

            Currencies targetCur = cdi.getByCode(targetCurrency);
            if (targetCur.getId() == 0) throw new CurrencyDidNotExist("Currency didn't exist in table");

            BigDecimal value = new BigDecimal(rate);
            double rateResult = value.setScale(6, RoundingMode.HALF_UP).doubleValue();

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

    private static boolean isValidRate(String rate) throws NumberFormatException {
        double rateDouble = Double.parseDouble(rate);
        return true;
    }

    private static boolean isValidCurrency(String currency) {
        return currency.length() == 3 && currency.matches("[A-Z]*");
    }
}
