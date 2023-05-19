package servlet;

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

@WebServlet("/exchangeRate/*")
public class GetExchangeRateByCodeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=utf-8");
        String exchangeRateField = req.getParameter("exchangeRateField");
        PrintWriter printWriter = resp.getWriter();

        if (exchangeRateField == null) {
            String pairReq = getPairFromUrl(req);
            if (isRequestExist(pairReq)) {
                try {
                    String answer = getExchangeRatePair(pairReq);
                    printWriter.write(answer);
                    resp.setStatus(HttpServletResponse.SC_OK);
                } catch (CurrencyPairIsNotValid | ExchangeRatesIsNotExistException e) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    printWriter.write(AlertMessage.MESSAGE_ERROR_CORRECT_FILL_FIELD);
                } catch (SQLException e) {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    printWriter.write(AlertMessage.MESSAGE_ERROR_WITH_WORK_BY_DATABASE);
                }
            }
        } else if (exchangeRateField.equals("") || !exchangeRateField.matches("[a-zA-Z]*")) {
            printWriter.write(AlertMessage.MESSAGE_ERROR_CORRECT_FILL_FIELD);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            String path = "/exchangeRate/" + exchangeRateField.toUpperCase();
            resp.sendRedirect(path);
        }
    }

    private static String getPairFromUrl(HttpServletRequest req) {
        String path = req.getRequestURL().toString();
        String[] pathPart = path.split("/");
        return pathPart[pathPart.length - 1].toUpperCase();
    }

    private static boolean isRequestExist(String str) {
        return !str.equals("exchangeRate");
    }
}
