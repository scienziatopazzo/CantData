package dev.vedcodee.it.types.models;

import dev.vedcodee.it.types.DataType;
import dev.vedcodee.it.types.models.number.DataNumber;

public class DataLong extends DataType<Long> implements DataNumber<Long>, Comparable<DataLong> {

    @Override
    public String serialize() {
        return String.valueOf(get());
    }

    @Override
    public void apply(String string) {
        try {
            set(Long.parseLong(string));
        } catch (NumberFormatException e) {
            set(empty());
            e.printStackTrace();
        }
    }

    @Override
    public Long empty() {
        return 0L;
    }

    @Override
    public void add(Long num) {
        set((get() != null ? get() : empty()) + num);
    }

    @Override
    public void remove(Long num) {
        set((get() != null ? get() : empty()) - num);
    }

    @Override
    public int compareTo(DataLong other) {
        return Long.compare(this.get(), other.get());
    }
}
