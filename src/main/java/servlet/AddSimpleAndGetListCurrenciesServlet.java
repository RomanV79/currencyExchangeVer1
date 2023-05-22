package servlet;

import MyException.CurrencyAlreadyExistsException;
import Utils.AlertMessage;
import dao.daoImpl.CurrenciesDaoImpl;
import entity.Currencies;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import static service.ObjectToJson.getListToJson;
import static service.ObjectToJson.getSimpleJson;

@WebServlet(urlPatterns = "/currencies")
public class AddSimpleAndGetListCurrenciesServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String name = req.getParameter("name");
        String code = req.getParameter("code").toUpperCase();
        String sign = req.getParameter("sign");

        CurrenciesDaoImpl cdi = new CurrenciesDaoImpl();
        PrintWriter out = resp.getWriter();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("utf-8");
        String result;

        if (!code.equals("") & !name.equals("") & !sign.equals("") & code.matches("[A-Z]*")) {
            try {
                int id = cdi.add(new Currencies(code, name, sign));
                result = getSimpleJson(cdi.getById(id));
                resp.setStatus(HttpServletResponse.SC_OK);
                out.println(result);
                out.flush();

            } catch (CurrencyAlreadyExistsException | SQLException e) {
                e.printStackTrace();
                if (e instanceof CurrencyAlreadyExistsException) {
                    resp.setStatus(HttpServletResponse.SC_CONFLICT);
                    out.print(AlertMessage.MESSAGE_CURRENCY_ALREADY_EXIST);
                    out.flush();
                } else {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.write(AlertMessage.MESSAGE_ERROR_WITH_WORK_BY_DATABASE);
                    out.flush();
                }
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write(AlertMessage.MESSAGE_ERROR_CORRECT_FILL_FIELD);
            out.flush();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        CurrenciesDaoImpl cdi = new CurrenciesDaoImpl();
        PrintWriter out = resp.getWriter();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("utf-8");

        try {
            resp.setStatus(HttpServletResponse.SC_OK);
            out.print(getListToJson(cdi.getAll()));
            out.flush();

        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(AlertMessage.MESSAGE_ERROR_WITH_WORK_BY_DATABASE);
            out.flush();
        }
    }
}
