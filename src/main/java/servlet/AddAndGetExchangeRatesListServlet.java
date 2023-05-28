package servlet;

import MyException.*;
import Utils.AlertMessage;
import dao.daoImpl.ExchangeRatesDao;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ExchangeRateService;
import service.ObjectToJson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import static service.ObjectToJson.getListToJson;

@WebServlet(urlPatterns = "/exchangeRates")
public class AddAndGetExchangeRatesListServlet extends HttpServlet {

    private static final ExchangeRateService exchangeRateService = new ExchangeRateService();
    private static final UtilServlet UTIL_SERVLET = new UtilServlet();
    private static final ExchangeRatesDao exDAO = new ExchangeRatesDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();

        try {
            out.print(getListToJson(exDAO.getAll()));
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (ServiceDidntAnswerException e) {
            out.format(AlertMessage.MESSAGE_ERROR, e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            out.flush();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, String[]> params = req.getParameterMap();
        String baseCurrency = params.get("baseCurrencyCode")[0];
        String targetCurrency = params.get("targetCurrencyCode")[0];

        PrintWriter out = resp.getWriter();

        if (!UTIL_SERVLET.isCurrencyReqValid(baseCurrency) || !UTIL_SERVLET.isCurrencyReqValid(targetCurrency)) {
            out.format(AlertMessage.MESSAGE_ERROR_CURRENCY_IS_NOT_VALID);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.flush();
            return;
        }

        String rate = params.get("rate")[0];
        if (!UTIL_SERVLET.isRateValid(rate)) {
            out.format(AlertMessage.MESSAGE_ERROR_RATE_IS_NOT_VALID);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.flush();
            return;
        }

        try {
            out.print(ObjectToJson.getSimpleJson(exchangeRateService.addExchangeRate(baseCurrency, targetCurrency, rate)));
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (ExchangeRateAlreadyExistException e) {
            out.format(AlertMessage.MESSAGE_ERROR, e);
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
        } catch (CurrencyDidNotExist e) {
            out.format(AlertMessage.MESSAGE_ERROR, e);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (NoIdReturnAfterAddException | ServiceDidntAnswerException e) {
            out.format(AlertMessage.MESSAGE_ERROR, e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            out.flush();
        }
    }
}
