package stress.vedcodee.it.data;

import dev.vedcodee.it.data.CantData;
import stress.vedcodee.it.Main;
import stress.vedcodee.it.data.values.StorageValue;

import java.util.UUID;

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

}
