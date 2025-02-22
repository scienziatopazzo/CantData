package dev.vedcodee.it.data;

import dev.vedcodee.it.types.DataEnum;
import dev.vedcodee.it.types.DataType;
import dev.vedcodee.it.types.DataTypes;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class CantData<E extends Enum<E> & DataEnum> {

    @Getter private final UUID uuid;
    private final Map<String, DataType<?>> datas;

    public CantData(UUID uuid, Class<E> dataEnumClass, String rawData) {
        this.uuid = uuid;
        this.datas = new ConcurrentHashMap<>();

        Enum<?>[] enumConstants = dataEnumClass.getEnumConstants();
        if (enumConstants == null) {
            throw new IllegalArgumentException("Provided class is not a valid enum");
        }

        for (Enum<?> constant : enumConstants) {
            try {
                Enum<?> type = (Enum<?>) dataEnumClass.getDeclaredField(constant.name()).get(null);
                DataType<?> dataType = DataTypes.of(((DataEnum) type).getType());
                dataType.init(this, constant);
                datas.put(constant.name(), dataType);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException("Failed to initialize data types", e);
            }
        }

        if(rawData != null) load(rawData);
    }

    @SuppressWarnings("unchecked")
    public <V> DataType<V> from(E data) {
        return (DataType<V>) datas.get(data.name());
    }

    public void load(String data) {
        String[] split = data.split("§");
        int i = 0;
        for (String key : datas.keySet()) {
            DataType<?> type = datas.get(key);
            if (type != null && i < split.length) {
                type.apply(split[i]);
            }
            i++;
        }
    }

    public String serialize() {
        StringBuilder data = new StringBuilder();
        boolean first = true;
        for (DataType<?> value : datas.values()) {
            if(first) first = false;
            else data.append("§");
            data.append(value.serialize());
        }
        return data.toString();
    }

    // boolean is the cancel. true = cancel, false = continue
    public boolean isCancelled(Enum<?> from, Object newValue) {
        return onChange((E) from, newValue);
    }

    public boolean onChange(E from, Object newValue) {
        return false;
    }


}
