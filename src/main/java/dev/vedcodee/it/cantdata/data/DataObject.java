package dev.vedcodee.it.cantdata.data;

import dev.vedcodee.it.cantdata.type.Type;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;

public abstract class DataObject {

    public abstract List<String> keys();

}
