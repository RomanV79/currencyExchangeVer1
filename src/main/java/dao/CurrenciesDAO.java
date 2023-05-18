package dao;

import MyException.CurrencyAlreadyExistsException;
import entity.Currencies;
import java.sql.SQLException;
import java.util.List;


public interface CurrenciesDAO {

    // create
    int add(Currencies currencies) throws SQLException, CurrencyAlreadyExistsException;

    // read
    Currencies getById(int id) throws SQLException;
    Currencies getByCode(String code) throws SQLException;
    List<Currencies> getAll() throws SQLException;

    // update
    void update(Currencies currencies) throws SQLException;

    boolean isExist(Currencies currencies) throws SQLException;
}
