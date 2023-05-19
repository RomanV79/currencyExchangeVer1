package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

public class ObjectToJson {
    public <T> String getSimpleJson(T t) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(t);
    }

    public <T> String getListToJson(List<T> t) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(t);
    }
}
