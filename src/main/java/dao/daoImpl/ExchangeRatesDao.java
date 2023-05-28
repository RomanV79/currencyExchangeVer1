package dao.daoImpl;

import MyException.*;
import Utils.UtilsDB;
import dao.DAO;
import entity.Currencies;
import entity.ExchangeRates;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRatesDao extends UtilsDB implements DAO<ExchangeRates> {

    List<ExchangeRates> exchangeRatesList = new ArrayList<>();
    private static final CurrenciesDao currenciesDao = new CurrenciesDao();
    @Override
    public int add(ExchangeRates exchangeRates) throws ExchangeRateAlreadyExistException,
            NoIdReturnAfterAddException,
            ServiceDidntAnswerException {

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
                    throw new NoIdReturnAfterAddException("Add is not success, no ID returned...");
                }
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("SQLITE_CONSTRAINT_UNIQUE")) {
                throw new ExchangeRateAlreadyExistException("Exchangerate is already exist");
            } else {
                throw new ServiceDidntAnswerException("Service didn't answer");
            }
        }

        return id;
    }

    @Override
    public Optional<ExchangeRates> getById(int id) throws ExchangeRatesIsNotExistException, ServiceDidntAnswerException {
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

        ExchangeRates exchangeRates;

        try (Connection connection = getConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);) {

            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            exchangeRates = new ExchangeRates();
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

            if (exchangeRates.getId() == 0) return Optional.empty();

        } catch (SQLException e) {
            throw new ServiceDidntAnswerException("Service didn't answer");
        }

        return Optional.ofNullable(exchangeRates);
    }

    @Override
    public void update(ExchangeRates exchangeRates) throws ServiceDidntAnswerException {
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
            throw new ServiceDidntAnswerException("Service didn't answer");
        }

    }

    @Override
    public List<ExchangeRates> getAll() throws ServiceDidntAnswerException {

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

                exchangeRatesList.add(exchangeRates);
            }
        } catch (SQLException e) {
            throw new ServiceDidntAnswerException("Service didn't answer");
        }
        return exchangeRatesList;
    }

    @Override
    public Optional<ExchangeRates> getByCode(String code) throws ServiceDidntAnswerException,
                                                                    CurrencyDidNotExist {
        code = code.toUpperCase();
        Currencies curBase = currenciesDao.getByCode(code.substring(0, 3)).get();
        Currencies curTarget = currenciesDao.getByCode(code.substring(3, 6)).get();
        ExchangeRates exchangeRates;

        String sql = "SELECT * FROM ExchangeRates\n" +
                    "WHERE BaseCurrencyId = ? and TargetCurrencyId = ?";

        try (Connection connection = getConnect();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, curBase.getId());
            preparedStatement.setInt(2, curTarget.getId());
            ResultSet resultSet = preparedStatement.executeQuery();

            exchangeRates = new ExchangeRates();
            exchangeRates.setId(resultSet.getInt("ID"));
            exchangeRates.setBaseCurrencyId(curBase);
            exchangeRates.setTargetCurrencyId(curTarget);
            exchangeRates.setRate(resultSet.getDouble("Rate"));
            if (exchangeRates.getId() == 0) exchangeRates = null;

        } catch (SQLException e) {
            throw new ServiceDidntAnswerException("Service didn't answer");
        }

        return Optional.ofNullable(exchangeRates);
    }

}
