package dev.vedcodee.it.types.models;

import dev.vedcodee.it.types.DataType;
import dev.vedcodee.it.types.models.number.DataNumber;

public class DataDouble extends DataType<Double> implements DataNumber<Double>, Comparable<DataDouble> {

    @Override
    public String serialize() {
        return String.valueOf(get());
    }

    @Override
    public void apply(String string) {
        try {
            set(Double.parseDouble(string));
        } catch (NumberFormatException e) {
            set(empty());
            e.printStackTrace();
        }
    }

    @Override
    public Double empty() {
        return 0.0;
    }

    @Override
    public void add(Double num) {
        set((get() != null ? get() : empty()) + num);
    }

    @Override
    public void remove(Double num) {
        set((get() != null ? get() : empty()) - num);
    }

    @Override
    public int compareTo(DataDouble other) {
        return Double.compare(this.get(), other.get());
    }
}
