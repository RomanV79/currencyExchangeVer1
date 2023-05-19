package dao;

import MyException.CurrencyAlreadyExistsException;
import entity.Currencies;
import entity.ExchangeRates;

import java.sql.SQLException;
import java.util.List;

public interface ExchangeRatesDAO {

    // create
    int add(ExchangeRates exchangeRates) throws CurrencyAlreadyExistsException;

    // read
    ExchangeRates getById(int id);
    List<ExchangeRates> getAll();
    ExchangeRates getExchangeRateByCurPair(Currencies curBase, Currencies curTarget);

    // update
    void update(ExchangeRates exchangeRates);



}
