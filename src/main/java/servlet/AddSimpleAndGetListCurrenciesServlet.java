package servlet;

import MyException.CurrencyAlreadyExistsException;
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
import java.util.Map;

import static service.ObjectToJson.getListToJson;
import static service.ObjectToJson.getSimpleJson;

@WebServlet(value = "/currencies")
public class AddSimpleAndGetListCurrenciesServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Map<String, String[]> params = request.getParameterMap();
        String codeCurrency = params.get("currency-code")[0].toUpperCase();
        String fullNameCurrency = params.get("currency-fullname")[0];
        String signCurrency = params.get("currency-sign")[0];

        CurrenciesDaoImpl cdi = new CurrenciesDaoImpl();
        response.setContentType("application/json;charset=utf-8");
        PrintWriter printWriter = response.getWriter();
        String result;

        if (!codeCurrency.equals("") & !fullNameCurrency.equals("") & !signCurrency.equals("") & codeCurrency.matches("[A-Z]*")) {
            try {
                int id = cdi.add(new Currencies(codeCurrency, fullNameCurrency, signCurrency));
                result = getSimpleJson(cdi.getById(id));
                response.setStatus(HttpServletResponse.SC_OK);
                printWriter.write(result);

            } catch (CurrencyAlreadyExistsException | SQLException e) {
                e.printStackTrace();
                if (e instanceof CurrencyAlreadyExistsException) {
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    printWriter.write(AlertMessage.MESSAGE_CURRENCY_ALREADY_EXIST);
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    printWriter.write(AlertMessage.MESSAGE_ERROR_WITH_WORK_BY_DATABASE);
                }
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            printWriter.write(AlertMessage.MESSAGE_ERROR_CORRECT_FILL_FIELD);
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        CurrenciesDaoImpl cdi = new CurrenciesDaoImpl();
        resp.setContentType("application/json;charset=utf-8");
        PrintWriter printWriter = resp.getWriter();

        try {
            printWriter.write(getListToJson(cdi.getAll()));
            resp.setStatus(HttpServletResponse.SC_OK);

        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            printWriter.write(AlertMessage.MESSAGE_ERROR_WITH_WORK_BY_DATABASE);
        }
    }
}
