package service;

import MyException.*;
import dao.daoImpl.CurrenciesDao;
import dao.daoImpl.ExchangeRatesDao;
import dto.ExchangeRatesDTO;
import entity.Currencies;
import entity.ExchangeRates;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;


public class ExchangeRateService {

    private final static CurrenciesDao curDao = new CurrenciesDao();
    private final static ExchangeRatesDao exDAO = new ExchangeRatesDao();
    private Optional<ExchangeRates> tempObj_1;
    private Optional<ExchangeRates> tempObj_2;

    public ExchangeRatesDTO getExchangeResult(String from, String to, String amount) throws CurrencyPairIsNotValid, RateOrAmountIsNotValid, CurrencyDidNotExist, ExchangeRatesIsNotExistException, ServiceDidntAnswerException {
        from = from.toUpperCase();
        to = to.toUpperCase();
        double amountDouble = Double.parseDouble(amount);

        ExchangeRates exchangeRate;
        String straightPair = from.concat(to);
        double convertedAmountRow;
        double rateRow;

        tempObj_1 = exDAO.getByCode(straightPair);
        if (tempObj_1.isPresent()) {
            exchangeRate = tempObj_1.get();
            rateRow = exchangeRate.getRate();
        } else {
            String reversePair = to.concat(from);
            tempObj_1 = exDAO.getByCode(reversePair);
            if (tempObj_1.isPresent()) {
                exchangeRate = tempObj_1.get();
                rateRow = 1 / exchangeRate.getRate();
            } else {
                String baseThrowUSD = from.concat("USD");
                String targetThrowUSD = "USD".concat(to);
                tempObj_1 = exDAO.getByCode(baseThrowUSD);
                tempObj_2 = exDAO.getByCode(targetThrowUSD);
                if (tempObj_1.isPresent() && tempObj_2.isPresent()) {
                    ExchangeRates baseRatesUSD = tempObj_1.get();
                    ExchangeRates targetRatesUSD = tempObj_2.get();
                    rateRow = baseRatesUSD.getRate() * targetRatesUSD.getRate();
                } else {
                    throw new ExchangeRatesIsNotExistException("Exchange rates not exist");
                }
            }
        }

        Currencies baseCur = curDao.getByCode(from).get();
        Currencies targetCur = curDao.getByCode(to).get();
        convertedAmountRow = amountDouble * rateRow;
        double convertedAmount = getDoubleFormat(convertedAmountRow, 2);
        double rate = getDoubleFormat(rateRow, 2);

        return new ExchangeRatesDTO(baseCur, targetCur, rate, amountDouble, convertedAmount);
    }

    private static double getDoubleFormat(double doubleValue, int newScale) {
        BigDecimal bigDecimal = new BigDecimal(doubleValue);
        return bigDecimal.setScale(newScale, RoundingMode.HALF_UP).doubleValue();
    }

    public ExchangeRates updateExchangeRate(String pair, String rate) throws CurrencyDidNotExist, ExchangeRatesIsNotExistException, ServiceDidntAnswerException {
        ExchangeRates exchangeRates = exDAO.getByCode(pair).get();
        double doubleRate = Double.parseDouble(rate);
        double rateResult = getDoubleFormat(doubleRate, 6);

        exchangeRates.setRate(rateResult);
        exDAO.update(exchangeRates);

        return exchangeRates;
    }

    public ExchangeRates addExchangeRate(String baseCurrency, String targetCurrency, String rate) throws CurrencyDidNotExist,
            NumberFormatException,
            ServiceDidntAnswerException,
            NoIdReturnAfterAddException,
            ExchangeRateAlreadyExistException {
        baseCurrency = baseCurrency.toUpperCase();
        targetCurrency = targetCurrency.toUpperCase();

        Currencies baseCur = curDao.getByCode(baseCurrency).get();
        if (baseCur.getId() == 0) throw new CurrencyDidNotExist("Currency didn't exist in table");

        Currencies targetCur = curDao.getByCode(targetCurrency).get();
        if (targetCur.getId() == 0) throw new CurrencyDidNotExist("Currency didn't exist in table");

        double doubleRate = Double.parseDouble(rate);
        double rateResult = getDoubleFormat(doubleRate, 6);

        ExchangeRates exchangeRates = new ExchangeRates(baseCur, targetCur, rateResult);
        int id = exDAO.add(exchangeRates);
        exchangeRates.setId(id);

        return exchangeRates;
    }
}
