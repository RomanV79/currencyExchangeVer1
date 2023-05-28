package dao;

import MyException.*;

import java.util.List;
import java.util.Optional;

public interface DAO<T> {

    // create
    int add (T t) throws CurrencyAlreadyExistsException, ServiceDidntAnswerException, NoIdReturnAfterAddException, ExchangeRateAlreadyExistException;

    // read
    Optional<T> getById(int id) throws ServiceDidntAnswerException, CurrencyDidNotExist, ExchangeRatesIsNotExistException;
    Optional<T> getByCode(String code) throws ServiceDidntAnswerException, CurrencyDidNotExist, ExchangeRatesIsNotExistException;
    List<T> getAll() throws ServiceDidntAnswerException;

    // update
    void update(T t) throws ServiceDidntAnswerException;





}
