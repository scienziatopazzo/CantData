package stress.vedcodee.it;

import dev.vedcodee.it.data.CantData;
import dev.vedcodee.it.database.auth.Auth;
import dev.vedcodee.it.types.DataType;
import dev.vedcodee.it.types.models.DataInteger;
import lombok.Getter;
import stress.vedcodee.it.data.StoragePlayer;
import stress.vedcodee.it.data.values.StorageValue;
import stress.vedcodee.it.database.Database;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Main {

    @Getter private static Database database;

    public static void main(String[] args) {
        database = new Database(new Auth("sd", "localhost", 3306, "root", ""), true);

        List<UUID> uuids = new CopyOnWriteArrayList<>();
        int numThreads = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        long startTime = System.currentTimeMillis();

        Random random = new Random();

        for (int i = 0; i < 10000; i++) {
            executor.execute(() -> {
                UUID uuid = UUID.randomUUID();
                uuids.add(uuid);

                StoragePlayer storagePlayer = new StoragePlayer(uuid);
                storagePlayer.from(StorageValue.STRING).set(uuid.toString());

                DataInteger dataInteger = (DataInteger) ((Object)storagePlayer.from(StorageValue.INT));
                dataInteger.add(random.nextInt());

                storagePlayer.save();
            });
        }

        executor.shutdown();

        long saveStartTime = System.currentTimeMillis();

        while (!executor.isTerminated()) {}
        System.out.println("Result: " + (saveStartTime - startTime) + "ms");

        startTime = System.currentTimeMillis();
        for (UUID uuid : uuids) {
            StoragePlayer storagePlayer = database.find(uuid, false);
            System.out.println("String: " + storagePlayer.from(StorageValue.STRING).get() + ", UUID: " + uuid);
        }
        saveStartTime = System.currentTimeMillis();
        System.out.println("Result: " + (saveStartTime - startTime) + "ms");

        System.out.println("Random: " + database.find(StorageValue.INT, 10, true).stream().map(s -> s.from(StorageValue.INT)).map(DataType::serialize).collect(Collectors.toList()));
    }

}
