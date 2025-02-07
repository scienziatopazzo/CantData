package dev.vedcodee.it.types.models;

import dev.vedcodee.it.types.DataType;
import dev.vedcodee.it.types.models.number.DataNumber;

public class DataFloat extends DataType<Float> implements DataNumber<Float>, Comparable<DataFloat> {

    @Override
    public String serialize() {
        return String.valueOf(get());
    }

    @Override
    public void apply(String string) {
        try {
            set(Float.parseFloat(string));
        } catch (NumberFormatException e) {
            set(empty());
            e.printStackTrace();
        }
    }

    @Override
    public Float empty() {
        return 0f;
    }

    @Override
    public void add(Float num) {
        set((get() != null ? get() : empty()) + num);
    }

    @Override
    public void remove(Float num) {
        set((get() != null ? get() : empty()) - num);
    }

    @Override
    public int compareTo(DataFloat other) {
        return Float.compare(this.get(), other.get());
    }
}
