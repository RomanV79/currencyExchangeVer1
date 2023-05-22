package servlet;

import MyException.CurrencyDidNotExist;
import MyException.CurrencyPairIsNotValid;
import MyException.ExchangeRatesIsNotExistException;
import Utils.AlertMessage;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import static service.ExchangeRateService.getExchangeRatePair;
import static service.ExchangeRateService.getResponseAfterUpdate;

@WebServlet(urlPatterns = "/exchangeRate/*")
public class GetExchangeRateByCodeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("utf-8");
        String exchangeRateField = req.getParameter("exchangeRateField");
        PrintWriter out = resp.getWriter();

        // эти две строки - костыль-заглушка для метода patch
        String methodType = req.getParameter("patch-form");
        if (methodType != null) return;

        if (exchangeRateField == null) {
            String pairReq = getPairFromUrl(req);
            if (isRequestExist(pairReq)) {
                try {
                    String answer = getExchangeRatePair(pairReq);
                    out.print(answer);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    out.flush();
                } catch (CurrencyPairIsNotValid | CurrencyDidNotExist | ExchangeRatesIsNotExistException e) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(AlertMessage.MESSAGE_ERROR_CORRECT_FILL_FIELD);
                    out.flush();
                } catch (SQLException e) {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print(AlertMessage.MESSAGE_ERROR_WITH_WORK_BY_DATABASE);
                    out.flush();
                }
            }
        } else if (exchangeRateField.equals("") || !exchangeRateField.matches("[a-zA-Z]*")) {
            out.print(AlertMessage.MESSAGE_ERROR_CORRECT_FILL_FIELD);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.flush();
        } else {
            String path = "/exchangeRate/" + exchangeRateField.toUpperCase();
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
        resp.setContentType("application/json");
        resp.setCharacterEncoding("utf-8");

        if ((isRequestExist(pair) || queryPatch != null) && !rate.equals("")) {
            try {
                out.print(getResponseAfterUpdate(pair, rate));
                resp.setStatus(HttpServletResponse.SC_OK);
            } catch (CurrencyPairIsNotValid e) {
                out.print(AlertMessage.MESSAGE_ERROR_CORRECT_FILL_FIELD);
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } catch (CurrencyDidNotExist |ExchangeRatesIsNotExistException e) {
                out.print(AlertMessage.MESSAGE_ERROR_CURRENCY_DOES_NOT_EXIST);
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
            out.flush();
        } else {
            out.print(AlertMessage.MESSAGE_ERROR_CORRECT_FILL_FIELD);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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
