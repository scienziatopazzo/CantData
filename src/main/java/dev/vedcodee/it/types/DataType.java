package dev.vedcodee.it.types;

public abstract class DataType<V> {

    private V data;

    // Empty Data type
    public DataType() {
        this.data = empty();
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
        this.data = data;
    }
}
