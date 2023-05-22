package servlet;

import MyException.CurrencyDidNotExist;
import Utils.AlertMessage;
import dao.daoImpl.CurrenciesDaoImpl;
import entity.Currencies;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import static service.ObjectToJson.getSimpleJson;

@WebServlet(urlPatterns = "/currency/*")
public class GetCurrencyByCodeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("utf-8");       
        PrintWriter out = resp.getWriter();

        String currencyCode = req.getParameter("currency-code");

        if (currencyCode == null) {
            String curCode = getCurrencyFromUrl(req);
            if (!isCurrencyExistInRequest(curCode)) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write(AlertMessage.MESSAGE_ERROR_CORRECT_FILL_FIELD);
            } else {
                CurrenciesDaoImpl cdi = new CurrenciesDaoImpl();

                try {
                    Currencies currencies = cdi.getByCode(curCode);
                    if (!cdi.isExist(currencies)) {
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.write(AlertMessage.MESSAGE_ERROR_CURRENCY_DOES_NOT_EXIST);
                    } else {
                        resp.setStatus(HttpServletResponse.SC_OK);
                        out.write(getSimpleJson(currencies));
                    }
                } catch (CurrencyDidNotExist e) {
                    out.print(AlertMessage.MESSAGE_ERROR_CURRENCY_DOES_NOT_EXIST);
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                } catch (SQLException e) {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.write(AlertMessage.MESSAGE_ERROR_WITH_WORK_BY_DATABASE);
                }
            }

        } else if (currencyCode.equals("") || !currencyCode.matches("[a-zA-Z]*")) {
            out.write(AlertMessage.MESSAGE_ERROR_CORRECT_FILL_FIELD);
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
