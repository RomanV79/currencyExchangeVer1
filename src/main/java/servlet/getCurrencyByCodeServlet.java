package servlet;

import Utils.AlertMessage;
import dao.daoImpl.CurrenciesDaoImpl;
import entity.Currencies;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ObjectToJson;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet("/currency/*")
public class getCurrencyByCodeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        PrintWriter printWriter = resp.getWriter();

        String currencyCode = req.getParameter("currency-code");

        if (currencyCode == null) {
            String curCode = getCurrencyFromUrl(req);
            System.out.println(curCode);
            if (!isCurrencyExistInRequest(curCode)) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                printWriter.write(AlertMessage.MESSAGE_ERROR_CORRECT_FILL_FIELD);
            } else {
                CurrenciesDaoImpl cdi = new CurrenciesDaoImpl();

                try {
                    Currencies currencies = cdi.getByCode(curCode);
                    ObjectToJson otj = new ObjectToJson();
                    if (!cdi.isExist(currencies)) {
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        printWriter.write(AlertMessage.MESSAGE_ERROR_CURRENCY_DOES_NOT_EXIST);
                    } else {
                        resp.setStatus(HttpServletResponse.SC_OK);
                        printWriter.write(otj.getSimpleJson(currencies));
                    }
                } catch (SQLException e) {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    printWriter.write(AlertMessage.MESSAGE_ERROR_WITH_WORK_BY_DATABASE);
                    throw new RuntimeException(e);
                }
            }

        } else if (currencyCode.equals("") || !currencyCode.matches("[a-zA-Z]*")) {
            printWriter.write(AlertMessage.MESSAGE_ERROR_CORRECT_FILL_FIELD);
        } else {
            String path = "/currency/" + currencyCode;
            resp.sendRedirect(path);

        }
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
