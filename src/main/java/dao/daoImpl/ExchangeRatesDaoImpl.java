package dao.daoImpl;

import MyException.CurrencyAlreadyExistsException;
import Utils.UtilsDB;
import dao.ExchangeRatesDAO;
import entity.Currencies;
import entity.ExchangeRates;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRatesDaoImpl extends UtilsDB implements ExchangeRatesDAO {

    @Override
    public int add(ExchangeRates exchangeRates) throws CurrencyAlreadyExistsException {
        String sql = "INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate)" +
                " VALUES(?, ?, ?)";
        int id = 0;


        try (Connection connection = getConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(sql))
        {
            preparedStatement.setInt(1, exchangeRates.getBaseCurrencyId().getId());
            preparedStatement.setInt(2, exchangeRates.getTargetCurrencyId().getId());
            preparedStatement.setDouble(3, exchangeRates.getRate());

            preparedStatement.executeUpdate();

            try (ResultSet resultId = preparedStatement.getGeneratedKeys()) {
                if (resultId.next()) {
                    id = resultId.getInt(1);
                } else {
                    connection.rollback();
                    throw new SQLException("No ID returned...");
                }
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("SQLITE_CONSTRAINT_UNIQUE")) {
                throw new CurrencyAlreadyExistsException("Currency already exist");
            }
        }

        return id;
    }

    @Override
    public ExchangeRates getById(int id) {
        String sql = "SELECT\n" +
                "    ER.ID AS ER_ID,\n" +
                "    CurBase.ID AS CurBase_ID,\n" +
                "    CurBase.Code AS CurBase_Code,\n" +
                "    CurBase.FullName AS CurBase_FullName,\n" +
                "    CurBase.Sign AS CurBase_Sign,\n" +
                "    CurTarg.ID AS CurTarg_ID,\n" +
                "    CurTarg.Code AS CurTarg_Code,\n" +
                "    CurTarg.FullName AS CurTarg_FullName,\n" +
                "    CurTarg.Sign AS Curtarg_Sign,\n" +
                "    ER.Rate as ER_Rate\n" +
                "FROM ExchangeRates AS ER\n" +
                "LEFT JOIN Currencies AS CurBase ON ER.BaseCurrencyId = CurBase.ID\n" +
                "LEFT JOIN Currencies AS CurTarg ON ER.TargetCurrencyId = CurTarg.ID\n" +
                "WHERE ER.ID = ?";

        ExchangeRates exchangeRates = new ExchangeRates();

        try (Connection connection = getConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);) {

            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            exchangeRates.setId(resultSet.getInt("ER_ID"));
            Currencies currenciesBase = new Currencies();
            currenciesBase.setId(resultSet.getInt("CurBase_ID"));
            currenciesBase.setCode(resultSet.getString("CurBase_Code"));
            currenciesBase.setFullName(resultSet.getString("CurBase_FullName"));
            currenciesBase.setSign(resultSet.getString("CurBase_Sign"));
            Currencies currenciesTarget = new Currencies();
            currenciesTarget.setId(resultSet.getInt("CurTarg_ID"));
            currenciesTarget.setCode(resultSet.getString("CurTarg_Code"));
            currenciesTarget.setFullName(resultSet.getString("CurTarg_FullName"));
            currenciesTarget.setSign(resultSet.getString("CurTarg_Sign"));
            exchangeRates.setRate(resultSet.getDouble("ER_Rate"));

            exchangeRates.setBaseCurrencyId(currenciesBase);
            exchangeRates.setTargetCurrencyId(currenciesTarget);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return exchangeRates;
    }

    @Override
    public void update(ExchangeRates exchangeRates) {
        String sql = "UPDATE ExchangeRates\n" +
                "SET BaseCurrencyId = ?, TargetCurrencyId = ?, Rate = ?\n" +
                "WHERE ID = ?";


        try (Connection connection = getConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);) {

            Currencies currenciesBase = exchangeRates.getBaseCurrencyId();
            Currencies currenciesTagret = exchangeRates.getTargetCurrencyId();
            preparedStatement.setInt(1, currenciesBase.getId());
            preparedStatement.setInt(2, currenciesTagret.getId());
            preparedStatement.setDouble(3, exchangeRates.getRate());
            preparedStatement.setInt(4, exchangeRates.getId());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<ExchangeRates> getAll() {
        List<ExchangeRates> resultList = new ArrayList<>();

        String sql = "SELECT\n" +
                "    ER.ID AS ER_ID,\n" +
                "    CurBase.ID AS CurBase_ID,\n" +
                "    CurBase.Code AS CurBase_Code,\n" +
                "    CurBase.FullName AS CurBase_FullName,\n" +
                "    CurBase.Sign AS CurBase_Sign,\n" +
                "    CurTarg.ID AS CurTarg_ID,\n" +
                "    CurTarg.Code AS CurTarg_Code,\n" +
                "    CurTarg.FullName AS CurTarg_FullName,\n" +
                "    CurTarg.Sign AS Curtarg_Sign,\n" +
                "    ER.Rate as ER_Rate\n" +
                "    FROM ExchangeRates AS ER\n" +
                "    LEFT JOIN Currencies AS CurBase ON ER.BaseCurrencyId = CurBase.ID\n" +
                "    LEFT JOIN Currencies AS CurTarg ON ER.TargetCurrencyId = CurTarg.ID";


        try (Connection connection = getConnect();
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                ExchangeRates exchangeRates = new ExchangeRates();

                exchangeRates.setId(resultSet.getInt("ER_ID"));
                Currencies currenciesBase = new Currencies();
                currenciesBase.setId(resultSet.getInt("CurBase_ID"));
                currenciesBase.setCode(resultSet.getString("CurBase_Code"));
                currenciesBase.setFullName(resultSet.getString("CurBase_FullName"));
                currenciesBase.setSign(resultSet.getString("CurBase_Sign"));
                Currencies currenciesTarget = new Currencies();
                currenciesTarget.setId(resultSet.getInt("CurTarg_ID"));
                currenciesTarget.setCode(resultSet.getString("CurTarg_Code"));
                currenciesTarget.setFullName(resultSet.getString("CurTarg_FullName"));
                currenciesTarget.setSign(resultSet.getString("CurTarg_Sign"));
                exchangeRates.setRate(resultSet.getDouble("ER_Rate"));

                exchangeRates.setBaseCurrencyId(currenciesBase);
                exchangeRates.setTargetCurrencyId(currenciesTarget);

                resultList.add(exchangeRates);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return resultList;
    }

    @Override
    public ExchangeRates getExchangeRateByCurPair(Currencies curBase, Currencies curTarget) {
        ExchangeRates exchangeRates = new ExchangeRates();
        String sql = "SELECT * FROM ExchangeRates\n" +
                    "WHERE BaseCurrencyId = ? and TargetCurrencyId = ?";

        try (Connection connection = getConnect();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, curBase.getId());
            preparedStatement.setInt(2, curTarget.getId());
            ResultSet resultSet = preparedStatement.executeQuery();

            exchangeRates.setId(resultSet.getInt("ID"));
            exchangeRates.setBaseCurrencyId(curBase);
            exchangeRates.setTargetCurrencyId(curTarget);
            exchangeRates.setRate(resultSet.getDouble("Rate"));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return exchangeRates;
    }
}
