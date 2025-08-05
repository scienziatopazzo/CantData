package dev.vedcodee.it.cantdata.data.serializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import dev.vedcodee.it.cantdata.data.DataObject;

public interface Manipulator<A extends DataObject> {

    JsonElement toJson(A object);
    A fromJson(JsonElement json);

}
