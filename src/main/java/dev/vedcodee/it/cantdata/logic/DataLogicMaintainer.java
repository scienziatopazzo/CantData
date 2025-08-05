package dev.vedcodee.it.cantdata.logic;

import com.google.gson.JsonElement;
import dev.vedcodee.it.cantdata.CantDataBootstrap;
import dev.vedcodee.it.cantdata.auth.ConnectionAuth;
import dev.vedcodee.it.cantdata.data.DataObject;
import dev.vedcodee.it.cantdata.data.serializer.Manipulator;
import dev.vedcodee.it.cantdata.type.Type;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class DataLogicMaintainer {

    private final CantDataBootstrap cantData;

    public DataLogicMaintainer(CantDataBootstrap cantData) {
        this.cantData = cantData;
    }

    public <A extends DataObject, B> B readOnly(Class<A> dataObject, String key, Function<A, B> reader) {
        return readOnly(dataObject, key, reader, null);
    }

    public <A extends DataObject, B> B readOnly(Class<A> dataObject, String key, Function<A, B> reader, A orElse) {
        Manipulator<A> manipulator = cantData.getDataObjectManipulator(dataObject);
        Type type = cantData.getDataObjectType(dataObject);
        String department = department(dataObject);
        A object = null;

        if (type == Type.PERSISTENT) {
            ConnectionAuth cachedDatabase = cantData.databases().get(Type.CACHED);
            String json = cachedDatabase.get(department, key);

            if (json == null) {
                ConnectionAuth persistentDatabase = cantData.databases().get(Type.PERSISTENT);
                json = persistentDatabase.get(department, key);

                if (json == null) return reader.apply(orElse);

                object = manipulator.fromJson(cantData.gson().fromJson(json, JsonElement.class));

                cachedDatabase.set(department, object.keys(), json);
                return reader.apply(object);
            }

            object = manipulator.fromJson(cantData.gson().fromJson(json, JsonElement.class));
            return reader.apply(object);
        } else if (type == Type.CACHED) {
            ConnectionAuth cachedDatabase = cantData.databases().get(Type.CACHED);
            String json = cachedDatabase.get(department, key);

            if (json == null) return reader.apply(orElse);

            object = manipulator.fromJson(cantData.gson().fromJson(json, JsonElement.class));
            cachedDatabase.set(department, object.keys(), json);

            return reader.apply(object);
        }

        return reader.apply(orElse);
    }

    public <A extends DataObject> void writeOnly(Class<A> dataObject, A object) {
        Manipulator<A> manipulator = cantData.getDataObjectManipulator(dataObject);
        Type type = cantData.getDataObjectType(dataObject);
        String department = department(dataObject);

        ConnectionAuth cachedDatabase = cantData.databases().get(Type.CACHED);
        String json = cantData.gson().toJson(manipulator.toJson(object));
        cachedDatabase.set(department, object.keys(), json);

        if (type == Type.PERSISTENT) {
            ConnectionAuth persistentDatabase = cantData.databases().get(Type.PERSISTENT);
            persistentDatabase.set(department, object.keys(), json);
        }
    }

    public <A extends DataObject> void modify(Class<A> dataObject, String key, Consumer<A> reader) {
        A object = readOnly(dataObject, key, (r) -> r);
        reader.accept(object);
        writeOnly(dataObject, object);
    }

    public <A extends DataObject> String department(Class<A> dataObject) {
        return dataObject.getName();
    }

}
