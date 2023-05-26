package dao.daoImpl;

import MyException.CurrencyAlreadyExistsException;
import MyException.CurrencyDidNotExist;
import MyException.NoIdReturnAfterAddException;
import MyException.ServiceDidntAnswerException;
import Utils.UtilsDB;
import dao.DAO;
import entity.Currencies;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrenciesDao extends UtilsDB implements DAO<Currencies> {

    private List<Currencies> currenciesList = new ArrayList<>();

    @Override
    public int add(Currencies currencies) throws CurrencyAlreadyExistsException,
                                                ServiceDidntAnswerException,
                                                NoIdReturnAfterAddException {
        int id = 0;

        String sql = "INSERT INTO Currencies (Code, FullName, Sign) " +
                "VALUES (?, ?, ?)";

        try (Connection connection = getConnect();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {

            preparedStatement.setString(1, currencies.getCode());
            preparedStatement.setString(2, currencies.getFullName());
            preparedStatement.setString(3, currencies.getSign());

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
                throw new CurrencyAlreadyExistsException("Currency already exist");
            } else {
                throw new ServiceDidntAnswerException("Service didn't answer");
            }

        }

        return id;
    }

    @Override
    public Optional<Currencies> getById(int id) throws ServiceDidntAnswerException,
                                                        CurrencyDidNotExist {

        Currencies currencies;
        String sql = "SELECT * FROM Currencies WHERE ID = ?";

        try (Connection connection = getConnect();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println(resultSet.getInt(1));

            currencies = new Currencies();
            currencies.setId(resultSet.getInt("ID"));
            currencies.setCode(resultSet.getString("Code"));
            currencies.setFullName(resultSet.getString("FullName"));
            currencies.setSign(resultSet.getString("Sign"));

            if (currencies.getId() == 0) throw new CurrencyDidNotExist("CurrencyDoesNotExist");

        } catch (SQLException e) {
            throw new ServiceDidntAnswerException("Service didn't answer");
        }

        return Optional.ofNullable(currencies);
    }

    @Override
    public Optional<Currencies> getByCode(String code) throws ServiceDidntAnswerException,
                                                                CurrencyDidNotExist {

        Currencies currencies;
        String sql = "SELECT * FROM Currencies WHERE Code = ?";

        try (Connection connection = getConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);) {

            preparedStatement.setString(1, code);
            ResultSet resultSet = preparedStatement.executeQuery();

            currencies = new Currencies();
            currencies.setId(resultSet.getInt("ID"));
            currencies.setCode(resultSet.getString("Code"));
            currencies.setFullName(resultSet.getString("FullName"));
            currencies.setSign(resultSet.getString("Sign"));

            if (currencies.getId() == 0) throw new CurrencyDidNotExist("CurrencyDoesNotExist");

        } catch (SQLException e) {
            throw new ServiceDidntAnswerException("Service didn't answer");
        }

        return Optional.ofNullable(currencies);
    }

    @Override
    public List<Currencies> getAll() throws ServiceDidntAnswerException {

        String sql = "SELECT * FROM Currencies";

        try (Connection connection = getConnect();
            Statement statement = connection.createStatement();) {

            ResultSet resultSet = statement.executeQuery(sql);

            while(resultSet.next()) {
                Currencies currencies = new Currencies();
                currencies.setId(resultSet.getInt("ID"));
                currencies.setCode(resultSet.getString("Code"));
                currencies.setFullName(resultSet.getString("FullName"));
                currencies.setSign(resultSet.getString("Sign"));

                currenciesList.add(currencies);
            }

        } catch (SQLException e) {
            throw new ServiceDidntAnswerException("Service didn't answer");
        }

        return currenciesList;
    }

    @Override
    public void update(Currencies currencies) throws ServiceDidntAnswerException {

        String sql = "UPDATE Currencies" +
                        " SET Code = ?, FullName = ?, Sign = ?" +
                        " WHERE ID = ?";

        try (Connection connection = getConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);) {

            preparedStatement.setString(1, currencies.getCode());
            preparedStatement.setString(2, currencies.getFullName());
            preparedStatement.setString(3, currencies.getSign());
            preparedStatement.setInt(4, currencies.getId());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new ServiceDidntAnswerException("Service didn't answer");
        }
    }

//    public boolean isExist(Currencies currencies) throws SQLException {
//        return currencies.getId() != 0;
//    }
}
