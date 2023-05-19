package servlet;

import MyException.CurrencyAlreadyExistsException;
import MyException.CurrencyDidNotExist;
import MyException.CurrencyPairIsNotValid;
import Utils.AlertMessage;
import dao.daoImpl.ExchangeRatesDaoImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Map;

import static service.ExchangeRateService.getResponseAfterAdd;
import static service.ObjectToJson.getListToJson;

@WebServlet("/exchangeRates")
public class AddAndGetExchangeRatesListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        ExchangeRatesDaoImpl erdi = new ExchangeRatesDaoImpl();
        PrintWriter printWriter = resp.getWriter();

        try {
            printWriter.write(getListToJson(erdi.getAll()));
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            printWriter.write(AlertMessage.MESSAGE_ERROR_WITH_WORK_BY_DATABASE);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=utf-8");
        Map<String, String[]> params = req.getParameterMap();
        String baseCurrency = params.get("baseCurrencyCode")[0];
        String targetCurrency = params.get("targetCurrencyCode")[0];
        String rate = params.get("rate")[0];

        PrintWriter printWriter = resp.getWriter();

        try {
            printWriter.write(getResponseAfterAdd(baseCurrency, targetCurrency, rate));
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (CurrencyPairIsNotValid | CurrencyDidNotExist e) {
            printWriter.write(AlertMessage.MESSAGE_ERROR_CORRECT_FILL_FIELD);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (CurrencyAlreadyExistsException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            printWriter.write(AlertMessage.MESSAGE_CURRENCY_ALREADY_EXIST);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
