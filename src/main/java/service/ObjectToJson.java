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

    public <T> String getListToJson(List<T> t) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(t);
    }
}
