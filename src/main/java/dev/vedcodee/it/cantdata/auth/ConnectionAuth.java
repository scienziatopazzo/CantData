package dev.vedcodee.it.cantdata.auth;

import java.util.List;
import java.util.Map;

public interface ConnectionAuth {

    void connect();
    void disconnect();

    void createDepartment(String department);

    void set(String department, List<String> keys, String json);
    void remove(String department, String key);
    String get(String department, String key);

    Map<List<String>, String> all(String department);
    void clear(String department);

}
