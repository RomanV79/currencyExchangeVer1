package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

public class ObjectToJson {
    public static <T> String getSimpleJson(T t) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(t);
    }

    public static <T> String getListToJson(List<T> t) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(t);
    }
}
