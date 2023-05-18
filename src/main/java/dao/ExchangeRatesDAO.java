package dao;

import entity.ExchangeRates;

public interface ExchangeRatesDAO {

    // create
    void add(ExchangeRates exchangeRates);

    // read
    ExchangeRates getById(int id);

    // update
    void update(ExchangeRates exchangeRates);



}
