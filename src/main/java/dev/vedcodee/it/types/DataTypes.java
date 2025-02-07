package dev.vedcodee.it.types;

import dev.vedcodee.it.types.models.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DataTypes {

    private static final Map<Class<?>, Class<? extends DataType<?>>> types = new ConcurrentHashMap<>();

    static {
        register(Double.class, DataDouble.class);
        register(Float.class, DataFloat.class);
        register(Integer.class, DataInteger.class);
        register(Long.class, DataLong.class);
        register(String.class, DataString.class);
        register(UUID.class, DataUUID.class);
    }

    public static <T> void register(Class<T> type, Class<? extends DataType<T>> dataType) {
        if (type == null || dataType == null) {
            throw new IllegalArgumentException("Type and DataType cannot be null");
        }
        types.put(type, dataType);
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<? extends DataType<T>> get(Class<T> type) {
        return (Class<? extends DataType<T>>) types.get(type);
    }

    public static <T> DataType<T> of(Class<T> type) {
        try {
            return get(type).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
