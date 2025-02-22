package test.vedcodee.it;

import dev.vedcodee.it.database.auth.Auth;
import dev.vedcodee.it.types.DataType;
import dev.vedcodee.it.types.models.DataInteger;
import lombok.Getter;
import test.vedcodee.it.data.StoragePlayer;
import test.vedcodee.it.data.values.StorageValue;
import test.vedcodee.it.database.Database;

import java.util.UUID;
import java.util.stream.Collectors;

public class Main {

    @Getter private static Database database;

    public static void main(String[] args) {
        database = new Database(new Auth("sd", "localhost", 3306, "root", ""), true);

        for (int i = 0; i < 30; i++) {
            UUID uuid = UUID.randomUUID();

            StoragePlayer storagePlayer = new StoragePlayer(uuid);
            storagePlayer.from(StorageValue.NAME).set("abc");
            storagePlayer.from(StorageValue.KILLS).set(10);
            storagePlayer.save();
        }


        System.out.println(database.find(StorageValue.KILLS, 100, true).stream().map(s -> (String)s.from(StorageValue.NAME).get() + ":" + s.from(StorageValue.KILLS).get()).collect(Collectors.toList()));
    }

}
