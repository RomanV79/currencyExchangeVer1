package servlet;

import MyException.CurrencyDidNotExist;
import MyException.ExchangeRatesIsNotExistException;
import MyException.ServiceDidntAnswerException;
import Utils.AlertMessage;
import dao.daoImpl.ExchangeRatesDao;
import entity.ExchangeRates;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ExchangeRateService;
import service.ObjectToJson;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Optional;


@WebServlet(urlPatterns = "/exchangeRate/*")
public class GetExchangeRateByCodeServlet extends HttpServlet {

    private static final UtilServlet UTIL_SERVLET = new UtilServlet();
    private static final ExchangeRateService exchangeRateService = new ExchangeRateService();
    private static final ExchangeRatesDao exDAO = new ExchangeRatesDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String exchangeRateField = req.getParameter("exchangeRateField");
        PrintWriter out = resp.getWriter();

        // эти две строки - костыль-заглушка для метода patch
        String methodType = req.getParameter("patch-form");
        if (methodType != null) return;

        if (exchangeRateField == null) {
            String pairReq = getPairFromUrl(req);
            if (isRequestExist(pairReq)) {
                System.out.println(pairReq);
                try {
                    Optional<ExchangeRates> tempObject = exDAO.getByCode(pairReq);
                    if (!tempObject.isPresent()) {
                        out.print(ObjectToJson.getSimpleJson(tempObject.get()));
                        resp.setStatus(HttpServletResponse.SC_OK);
                    } else {
                        out.print(AlertMessage.MESSAGE_ERROR_EXCHANGE_RATE_DOES_NOT_EXIST);
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    }

                } catch (CurrencyDidNotExist e) {
                    out.format(AlertMessage.MESSAGE_ERROR, e);
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                } catch (ServiceDidntAnswerException e) {
                    out.format(AlertMessage.MESSAGE_ERROR, e);
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                } finally {
                    out.flush();
                }
            }
        } else if (exchangeRateField.equals("") || !exchangeRateField.matches("[a-zA-Z]*")) {
            out.print(AlertMessage.MESSAGE_ERROR_CORRECT_FILL_FIELD);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.flush();
        } else {
            String fullPath = req.getRequestURL().toString();
            String path = fullPath + "/" + exchangeRateField.toUpperCase();
            resp.sendRedirect(path);
            out.flush();
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String methodType = req.getParameter("patch-form");

        String method = req.getMethod();
        if (method.equals("PATCH") || methodType != null) {
            try {
                this.doPatch(req, resp);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        this.doGet(req, resp);
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, SQLException {

        String pair;
        String queryPatch = req.getParameter("patch-form");
        String rate = "";

        if (queryPatch != null) {
            pair = queryPatch;
            rate = req.getParameter("rate-form");
        } else {
            pair = getPairFromUrl(req);
            String requestStr = req.getReader().readLine();
            rate = requestStr.replace("rate=", "");
        }

        PrintWriter out = resp.getWriter();

        if (!UTIL_SERVLET.isExchangePairValid(pair)) {
            out.print(AlertMessage.MESSAGE_ERROR_CURRENCY_IS_NOT_VALID);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.flush();
            return;
        }

        if (!UTIL_SERVLET.isRateValid(rate)) {
            out.format(AlertMessage.MESSAGE_ERROR_RATE_IS_NOT_VALID);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.flush();
            return;
        }

        System.out.println("pair = " + pair);
        try {
            out.print(ObjectToJson.getSimpleJson(exchangeRateService.updateExchangeRate(pair, rate)));
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (CurrencyDidNotExist | ExchangeRatesIsNotExistException e) {
            out.print(AlertMessage.MESSAGE_ERROR_CURRENCY_DOES_NOT_EXIST);
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (ServiceDidntAnswerException e) {
            out.format(AlertMessage.MESSAGE_ERROR, e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            out.flush();
        }
    }

    private static String getPairFromUrl(HttpServletRequest req) {
        String path = req.getRequestURL().toString();
        String[] pathPart = path.split("/");
        return pathPart[pathPart.length - 1];
    }

    private static boolean isRequestExist(String str) {
        return !str.equals("exchangeRate");
    }
}
