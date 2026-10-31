package app.parking.repos;

import java.util.HashMap;
import java.util.Map;

public class InMemoryRepository<T> {

    protected Map<String, T> data = new HashMap<>();

    public void save(String id, T object) {
        if (data.containsKey(id)) {
            throw new IllegalArgumentException("ID already exists.");
        }
        data.put(id, object);
    }

    public T findById(String id) {
        return data.get(id);
    }

    public void delete(String id) {
        data.remove(id);
    }

    public Map<String, T> findAll() {
        return data;
    }
}