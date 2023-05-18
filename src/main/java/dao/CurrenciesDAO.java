package dao;

import MyException.CurrencyAlreadyExistsException;
import entity.Currencies;
import java.sql.SQLException;
import java.util.List;


public interface CurrenciesDAO {

    // create
    public int add(Currencies currencies) throws SQLException, CurrencyAlreadyExistsException;

    // read
    public Currencies getById(int id) throws SQLException;
    public Currencies getByCode(String code) throws SQLException;
    public List<Currencies> getAll() throws SQLException;

    // update
    public void update(Currencies currencies) throws SQLException;
}
