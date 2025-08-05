package dev.vedcodee.it.cantdata;

import com.google.gson.Gson;
import dev.vedcodee.it.cantdata.auth.ConnectionAuth;
import dev.vedcodee.it.cantdata.data.DataObject;
import dev.vedcodee.it.cantdata.data.serializer.Manipulator;
import dev.vedcodee.it.cantdata.logic.DataLogicMaintainer;
import dev.vedcodee.it.cantdata.type.Type;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

public class CantDataBootstrap {

    private final Map<Class<? extends DataObject>, Manipulator> dataObjectManipulators;
    private final Map<Class<? extends DataObject>, Type> dataObjectTypes;
    @Getter @Accessors(fluent = true)
    private final Map<Type, ConnectionAuth> databases;
    @Getter @Accessors(fluent = true)
    private final Gson gson;
    @Getter @Accessors(fluent = true)
    private final DataLogicMaintainer dataLogicMaintainer;

    public CantDataBootstrap() {
        this.dataObjectManipulators = new HashMap<>();
        this.dataObjectTypes = new HashMap<>();
        this.databases = new HashMap<>();
        this.gson = new Gson();
        this.dataLogicMaintainer = new DataLogicMaintainer(this);
    }

    public <A extends DataObject> void add(Class<A> dataObject, Manipulator<A> manipulator, Type type) {
        dataObjectManipulators.put(dataObject, manipulator);
        dataObjectTypes.put(dataObject, type);
    }

    public <A extends DataObject> void remove(Class<A> dataObject) {
        dataObjectManipulators.remove(dataObject);
        dataObjectTypes.remove(dataObject);
    }

    public void setDatabase(Type type, ConnectionAuth connectionAuth) {
        databases.put(type, connectionAuth);
    }

    public <A extends DataObject> Manipulator<A> getDataObjectManipulator(Class<A> dataObject) {
        return dataObjectManipulators.get(dataObject);
    }

    public <A extends DataObject> Type getDataObjectType(Class<A> dataObject) {
        return dataObjectTypes.get(dataObject);
    }

    public void initialize() {
        for (ConnectionAuth value : databases.values()) {
            value.connect();
        }

        for (ConnectionAuth value : databases.values()) {
            for (Class<? extends DataObject> clazz : dataObjectTypes.keySet()) {
                value.createDepartment(clazz.getName());
            }
        }
    }



}
