package dev.vedcodee.it.types.models;

import dev.vedcodee.it.types.DataType;
import dev.vedcodee.it.types.models.number.DataNumber;

public class DataInteger extends DataType<Integer> implements DataNumber<Integer>, Comparable<DataInteger> {

    @Override
    public String serialize() {
        return String.valueOf(get());
    }

    @Override
    public void apply(String string) {
        try {
            set(Integer.parseInt(string));
        } catch (NumberFormatException e) {
            set(empty());
            e.printStackTrace();
        }
    }

    @Override
    public Integer empty() {
        return 0;
    }

    @Override
    public void add(Integer num) {
        set((get() != null ? get() : empty()) + num);
    }

    @Override
    public void remove(Integer num) {
        set((get() != null ? get() : empty()) - num);
    }

    @Override
    public int compareTo(DataInteger other) {
        return Integer.compare(this.get(), other.get());
    }
}
