package stress.vedcodee.it.data.values;

import dev.vedcodee.it.types.DataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor @Getter
public enum StorageValue implements DataEnum {

    STRING(String.class),
    INT(Integer.class),
    FLOAT(Float.class),
    LONG(Long.class),
    DOUBLE(Double.class),
    UUID(java.util.UUID.class),
    ;

    private final Class<?> type;

}
