package test.vedcodee.it.data.values;

import dev.vedcodee.it.types.DataEnum;
import dev.vedcodee.it.types.DataType;
import dev.vedcodee.it.types.models.DataString;
import dev.vedcodee.it.types.models.DataUUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor @Getter
public enum StorageValue implements DataEnum {

    NAME(String.class),
    KILLS(Integer.class),
    ;

    private final Class<?> type;

}
