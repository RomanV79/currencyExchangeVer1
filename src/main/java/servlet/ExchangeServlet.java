package servlet;

import MyException.*;
import Utils.AlertMessage;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ExchangeRateService;
import service.ObjectToJson;
import java.io.IOException;
import java.io.PrintWriter;


@WebServlet(urlPatterns = "/exchange")
public class ExchangeServlet extends HttpServlet {

    private static final UtilServlet UTIL_SERVLET = new UtilServlet();
    private static final ExchangeRateService erService = new ExchangeRateService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String from = req.getParameter("from");
        String to = req.getParameter("to");
        String amount = req.getParameter("amount");

        PrintWriter out = resp.getWriter();

        if (!UTIL_SERVLET.isCurrencyReqValid(from) || !UTIL_SERVLET.isCurrencyReqValid(to) || from.toUpperCase().equals(to.toUpperCase())) {
            out.format(AlertMessage.MESSAGE_ERROR_CURRENCY_IS_NOT_VALID);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.flush();
            return;
        }

        if (!UTIL_SERVLET.isRateValid(amount)) {
            out.format(AlertMessage.MESSAGE_ERROR_RATE_IS_NOT_VALID);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.flush();
            return;
        }

        try {
            String result = ObjectToJson.getSimpleJson(erService.getExchangeResult(from, to, amount));
            out.print(result);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (CurrencyPairIsNotValid | RateOrAmountIsNotValid | ExchangeRatesIsNotExistException e) {
            out.format(AlertMessage.MESSAGE_ERROR, e);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (CurrencyDidNotExist | ServiceDidntAnswerException e) {
            out.format(AlertMessage.MESSAGE_ERROR, e);
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } finally {
            out.flush();
        }
    }
}
