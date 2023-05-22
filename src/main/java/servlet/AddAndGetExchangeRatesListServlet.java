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

@WebServlet(urlPatterns = "*/exchangeRates")
public class AddAndGetExchangeRatesListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("utf-8");
        ExchangeRatesDaoImpl erdi = new ExchangeRatesDaoImpl();

        try {
            out.print(getListToJson(erdi.getAll()));
            resp.setStatus(HttpServletResponse.SC_OK);
            out.flush();
        } catch (Exception e) {
            out.print(AlertMessage.MESSAGE_ERROR_WITH_WORK_BY_DATABASE);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.flush();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("utf-8");
        Map<String, String[]> params = req.getParameterMap();
        String baseCurrency = params.get("baseCurrencyCode")[0];
        String targetCurrency = params.get("targetCurrencyCode")[0];
        String rate = params.get("rate")[0];

        PrintWriter out = resp.getWriter();

        try {
            out.print(getResponseAfterAdd(baseCurrency, targetCurrency, rate));
            resp.setStatus(HttpServletResponse.SC_OK);
            out.flush();
        } catch (CurrencyPairIsNotValid | CurrencyDidNotExist e) {
            out.print(AlertMessage.MESSAGE_ERROR_CORRECT_FILL_FIELD);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.flush();
        } catch (CurrencyAlreadyExistsException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            out.print(AlertMessage.MESSAGE_CURRENCY_ALREADY_EXIST);
            out.flush();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
