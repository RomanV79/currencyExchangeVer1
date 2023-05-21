package dao;

import MyException.CurrencyAlreadyExistsException;
import MyException.ExchangeRatesIsNotExistException;
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
    ExchangeRates getExchangeRateByCurPair(Currencies curBase, Currencies curTarget) throws ExchangeRatesIsNotExistException;

    // update
    void update(ExchangeRates exchangeRates);



}
