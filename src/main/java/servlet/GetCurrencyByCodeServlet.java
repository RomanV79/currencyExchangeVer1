package servlet;

import MyException.CurrencyDidNotExist;
import MyException.ServiceDidntAnswerException;
import Utils.AlertMessage;
import dao.daoImpl.CurrenciesDao;
import entity.Currencies;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


import static service.ObjectToJson.getSimpleJson;

@WebServlet(urlPatterns = "/currency/*")
public class GetCurrencyByCodeServlet extends HttpServlet {

    private static final CurrenciesDao curDAO = new CurrenciesDao();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        PrintWriter out = resp.getWriter();

        String currencyCode = req.getParameter("currency-code");

        if (currencyCode == null) {
            currencyCode = getCurrencyFromUrl(req);
            if (!isCurrencyExistInRequest(currencyCode)) {
                out.print(AlertMessage.MESSAGE_ERROR_CORRECT_FILL_FIELD);
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.flush();
            } else {
                try {
                    Currencies currencies = curDAO.getByCode(currencyCode).get();
                    out.print(getSimpleJson(currencies));
                    resp.setStatus(HttpServletResponse.SC_OK);
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

        } else if (currencyCode.equals("") || !currencyCode.matches("[a-zA-Z]*")) {
            out.print(AlertMessage.MESSAGE_ERROR_CORRECT_FILL_FIELD);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            String fullPath = req.getRequestURL().toString();
            String path = fullPath + "/" + currencyCode;
            resp.sendRedirect(path);
        }
        out.flush();
    }

    private static String getCurrencyFromUrl(HttpServletRequest req) {
        String path = req.getRequestURL().toString();
        String[] pathPart = path.split("/");
        return pathPart[pathPart.length - 1].toUpperCase();
    }

    private boolean isCurrencyExistInRequest(String stringUrl) {
        return !stringUrl.equals("CURRENCY");
    }
}
