package servlet;

import MyException.CurrencyAlreadyExistsException;
import MyException.CurrencyDidNotExist;
import MyException.NoIdReturnAfterAddException;
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
import static service.ObjectToJson.getListToJson;
import static service.ObjectToJson.getSimpleJson;

@WebServlet(urlPatterns = "/currencies")
public class AddSimpleAndGetListCurrenciesServlet extends HttpServlet {
    private static final UtilServlet UTIL_SERVLET = new UtilServlet();

    private static final CurrenciesDao curDAO = new CurrenciesDao();
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String code = req.getParameter("code").toUpperCase();
        String name = req.getParameter("name");
        String sign = req.getParameter("sign");

        PrintWriter out = resp.getWriter();
        String result;

        System.out.println(UTIL_SERVLET.isCurrencyReqValid(code));
        if (!UTIL_SERVLET.isCurrencyReqValid(code)) {
            out.println(AlertMessage.MESSAGE_ERROR_CURRENCY_IS_NOT_VALID);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.flush();
            return;
        }

        if (name.equals("") || sign.equals("")) {
            out.println(AlertMessage.MESSAGE_ERROR_CORRECT_FILL_FIELD);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.flush();
            return;
        }

        try {
            int id = curDAO.add(new Currencies(code, name, sign));
            result = getSimpleJson(curDAO.getById(id));
            resp.setStatus(HttpServletResponse.SC_OK);
            out.println(result);

        } catch (CurrencyAlreadyExistsException e) {
            out.format(AlertMessage.MESSAGE_ERROR, e);
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
        } catch (NoIdReturnAfterAddException e) {
            out.format(AlertMessage.MESSAGE_ERROR, e);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();

        try {
            resp.setStatus(HttpServletResponse.SC_OK);
            out.print(getListToJson(curDAO.getAll()));
            out.flush();

        } catch (ServiceDidntAnswerException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(AlertMessage.MESSAGE_ERROR_WITH_WORK_BY_DATABASE);
            out.flush();
        }
    }
}
