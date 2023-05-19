package dao;

import entity.ExchangeRates;

import java.util.List;

public interface ExchangeRatesDAO {

    // create
    void add(ExchangeRates exchangeRates);

    // read
    ExchangeRates getById(int id);
    List<ExchangeRates> getAll();

    // update
    void update(ExchangeRates exchangeRates);



}
