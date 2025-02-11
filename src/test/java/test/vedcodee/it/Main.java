package test.vedcodee.it;

import dev.vedcodee.it.database.auth.Auth;
import dev.vedcodee.it.types.DataType;
import dev.vedcodee.it.types.models.DataInteger;
import lombok.Getter;
import test.vedcodee.it.data.StoragePlayer;
import test.vedcodee.it.data.values.StorageValue;
import test.vedcodee.it.database.Database;

import java.util.UUID;

public class Main {

    @Getter private static Database database;

    public static void main(String[] args) {
        database = new Database(new Auth("sd", "localhost", 3306, "root", ""), true);

        //UUID uuid = UUID.randomUUID();
        /*
        StoragePlayer storagePlayer = new StoragePlayer(uuid);
        storagePlayer.from(StorageValue.NAME).set("abc");
        storagePlayer.save();
         */


        System.out.println("Storage: " + database.find(StorageValue.NAME, "abc"));
    }

}
