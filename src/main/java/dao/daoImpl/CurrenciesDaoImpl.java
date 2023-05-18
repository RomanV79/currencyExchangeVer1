package dao.daoImpl;

import MyException.CurrencyAlreadyExistsException;
import Utils.UtilsDB;
import dao.CurrenciesDAO;
import entity.Currencies;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrenciesDaoImpl extends UtilsDB implements CurrenciesDAO {

    @Override
    public int add(Currencies currencies) throws CurrencyAlreadyExistsException, SQLException {
        Connection connection = getConnect();

        PreparedStatement preparedStatement = null;
        int id = 0;

        String sql = "INSERT INTO Currencies (Code, FullName, Sign) " +
                "VALUES (?, ?, ?)";

        try {
            preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, currencies.getCode());
            preparedStatement.setString(2, currencies.getFullName());
            preparedStatement.setString(3, currencies.getSign());

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

        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }

        return id;
    }

    @Override
    public Currencies getById(int id) throws SQLException {
        Connection connection = getConnect();

        Currencies currencies = new Currencies();
        PreparedStatement preparedStatement = null;

        String sql = "SELECT * FROM Currencies WHERE ID = ?";

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            currencies.setId(resultSet.getInt("ID"));
            currencies.setCode(resultSet.getString("Code"));
            currencies.setFullName(resultSet.getString("FullName"));
            currencies.setSign(resultSet.getString("Sign"));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }

        return currencies;
    }

    @Override
    public Currencies getByCode(String code) throws SQLException {
        Connection connection = getConnect();

        Currencies currencies = new Currencies();
        PreparedStatement preparedStatement = null;
        String sql = "SELECT * FROM Currencies WHERE Code = ?";

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, code);
            ResultSet resultSet = preparedStatement.executeQuery();

            currencies.setId(resultSet.getInt("ID"));
            currencies.setCode(resultSet.getString("Code"));
            currencies.setFullName(resultSet.getString("FullName"));
            currencies.setSign(resultSet.getString("Sign"));

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }

        return currencies;
    }

    @Override
    public List<Currencies> getAll() throws SQLException {
        Connection connection = getConnect();

        List<Currencies> currenciesList = new ArrayList<>();
        String sql = "SELECT * FROM Currencies";
        Statement statement = null;

        try {
            statement = connection.createStatement();
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
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }

        return currenciesList;
    }

    @Override
    public void update(Currencies currencies) throws SQLException {
        Connection connection = getConnect();
        PreparedStatement preparedStatement = null;

        String sql = "UPDATE Currencies" +
                " SET Code = ?, FullName = ?, Sign = ?" +
                " WHERE ID = ?";

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, currencies.getCode());
            preparedStatement.setString(2, currencies.getFullName());
            preparedStatement.setString(3, currencies.getSign());
            preparedStatement.setInt(4, currencies.getId());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public boolean isExist(Currencies currencies) throws SQLException {
        return currencies.getId() != 0;
    }
}
