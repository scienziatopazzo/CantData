package dev.vedcodee.it.types.models;

import dev.vedcodee.it.types.DataType;

public class DataString extends DataType<String> implements Comparable<DataString> {

    @Override
    public String serialize() {
        return get();
    }

    @Override
    public void apply(String string) {
        set(string);
    }

    @Override
    public String empty() {
        return "";
    }

    @Override
    public int compareTo(DataString other) {
        return this.get().compareToIgnoreCase(other.get());
    }
}
