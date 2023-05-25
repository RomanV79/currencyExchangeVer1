package servlet;

import MyException.CurrencyDidNotExist;
import MyException.CurrencyPairIsNotValid;
import MyException.ExchangeRatesIsNotExistException;
import MyException.RateOrAmountIsNotValid;
import Utils.AlertMessage;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ObjectToJson;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import static service.ExchangeRateService.getExchangeResult;

@WebServlet(urlPatterns = "/exchange")
public class ExchangeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String from = req.getParameter("from");
        String to = req.getParameter("to");
        String amount = req.getParameter("amount");

        resp.setContentType("application/json");
        resp.setCharacterEncoding("utf-8");
        PrintWriter out = resp.getWriter();

        try {
            String result = ObjectToJson.getSimpleJson(getExchangeResult(from, to, amount));
            out.print(result);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (CurrencyPairIsNotValid | RateOrAmountIsNotValid | ExchangeRatesIsNotExistException e) {
            out.print(AlertMessage.MESSAGE_ERROR_CORRECT_FILL_FIELD);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (CurrencyDidNotExist e) {
            out.print(AlertMessage.MESSAGE_ERROR_CURRENCY_DOES_NOT_EXIST);
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (SQLException e) {
            out.print(AlertMessage.MESSAGE_ERROR_WITH_WORK_BY_DATABASE);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        out.flush();
    }
}
