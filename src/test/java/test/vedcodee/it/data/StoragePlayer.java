package test.vedcodee.it.data;

import dev.vedcodee.it.data.CantData;
import dev.vedcodee.it.types.models.DataInteger;
import dev.vedcodee.it.types.models.DataString;
import test.vedcodee.it.Main;
import test.vedcodee.it.data.values.StorageValue;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class StoragePlayer extends CantData<StorageValue> {

    public StoragePlayer(UUID uuid, String data) {
        super(uuid, StorageValue.class, data);
    }

    public StoragePlayer(UUID uuid) {
        super(uuid, StorageValue.class, null);
    }

    public void save() {
        Main.getDatabase().save(this);
    }

    @Override
    public boolean onChange(StorageValue value, Object newValue) {
        if(value == StorageValue.KILLS) {
            int kills = (int) newValue;
            if(kills == 10) {
                return true;
            }
        }
        return false;
    }

}
