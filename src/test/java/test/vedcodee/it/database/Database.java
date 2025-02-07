package test.vedcodee.it.database;

import dev.vedcodee.it.database.CantDatabase;
import dev.vedcodee.it.database.auth.Auth;
import lombok.Getter;
import test.vedcodee.it.data.StoragePlayer;

import java.util.UUID;

@Getter
public class Database extends CantDatabase<StoragePlayer> {

    public Database(Auth auth, boolean cached) {
        super(auth, cached, "test");
    }

    protected StoragePlayer createData(UUID id, String data) {
        return new StoragePlayer(id, data);
    }

}
