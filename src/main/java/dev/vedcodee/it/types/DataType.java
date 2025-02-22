package dev.vedcodee.it.types;

import dev.vedcodee.it.data.CantData;

public abstract class DataType<V> {

    private V data;
    private CantData<?> parent;
    private Enum<?> enumKey;

    // Empty Data type
    public DataType() {
        this.data = empty();
    }

    public void init(CantData<?> parent, Enum<?> enumKey) {
        this.parent = parent;
        this.enumKey = enumKey;
    }

    // with this we can add this data to the database
    public abstract String serialize();
    // with this we can deserialize data from database;
    public abstract void apply(String string);
    // with this, we create a normal data (ex. if v = Integer, empty = 0)
    public abstract V empty();

    public V get() {
        return data;
    }

    public void set(V data) {
        if (parent != null && enumKey != null) {
            boolean cancel = parent.isCancelled(enumKey, data);
            if (cancel) return;
        }
        this.data = data;
    }

}
