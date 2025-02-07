package dev.vedcodee.it.types.models;

import dev.vedcodee.it.types.DataType;

import java.util.UUID;

public class DataUUID extends DataType<UUID> implements Comparable<DataUUID> {

    @Override
    public String serialize() {
        return get().toString();
    }

    @Override
    public void apply(String string) {
        set(UUID.fromString(string));
    }

    @Override
    public UUID empty() {
        return new UUID(0L, 0L);
    }

    @Override
    public int compareTo(DataUUID other) {
        return this.get().compareTo(other.get());
    }
}
