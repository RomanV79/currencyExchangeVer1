package servlet;

import Utils.AlertMessage;
import dao.daoImpl.ExchangeRatesDaoImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ObjectToJson;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/exchangeRates")
public class AddAndGetExchangeRatesListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        ExchangeRatesDaoImpl erdi = new ExchangeRatesDaoImpl();
        PrintWriter printWriter = resp.getWriter();
        ObjectToJson otj = new ObjectToJson();

        try {
            printWriter.write(otj.getListToJson(erdi.getAll()));
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            printWriter.write(AlertMessage.MESSAGE_ERROR_WITH_WORK_BY_DATABASE);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
