package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entity.Currencies;

import java.util.List;

public class ObjectToJson {
    public String getSimpleJson(Currencies currencies) {
        String jsonStr;

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        jsonStr = gson.toJson(currencies);

        return jsonStr;
    }

    public String getListToJson(List<Currencies> currenciesList) {
        String jsonStr = "";

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        jsonStr = gson.toJson(currenciesList);

        return jsonStr;
    }
}
