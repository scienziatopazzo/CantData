package stress.vedcodee.it;

import common.vedcodee.it.database.MySQLConnection;
import common.vedcodee.it.database.RedisConnector;
import common.vedcodee.it.playerdata.PlayerData;
import common.vedcodee.it.playerdata.serializer.PlayerDataSerializer;
import common.vedcodee.it.utils.Auth;
import dev.vedcodee.it.cantdata.CantDataBootstrap;
import dev.vedcodee.it.cantdata.type.Type;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        CantDataBootstrap cantData = new CantDataBootstrap();
        cantData.add(PlayerData.class, new PlayerDataSerializer(), Type.PERSISTENT);
        MySQLConnection mySQLConnection = new MySQLConnection(new Auth("test1", "localhost", 3306, "root", ""));
        RedisConnector redisConnector = new RedisConnector("127.0.0.1", 6379, "", "");
        cantData.setDatabase(Type.PERSISTENT, mySQLConnection);
        cantData.setDatabase(Type.CACHED, redisConnector);
        cantData.initialize();

        int parallelism = 50;
        int operationsPerTask = 500;

        ForkJoinPool pool = new ForkJoinPool(parallelism);

        LongAdder writeOps = new LongAdder();
        LongAdder readOps = new LongAdder();
        LongAdder modifyOps = new LongAdder();
        LongAdder errors = new LongAdder();

        ConcurrentHashMap<String, UUID> trackedPlayers = new ConcurrentHashMap<>();

        ScheduledExecutorService monitor = Executors.newSingleThreadScheduledExecutor();
        AtomicInteger secondsPassed = new AtomicInteger();

        monitor.scheduleAtFixedRate(() -> {
            int seconds = secondsPassed.incrementAndGet();
            long w = writeOps.sumThenReset();
            long r = readOps.sumThenReset();
            long m = modifyOps.sumThenReset();
            long e = errors.sum();
            System.out.printf("[%ds] Write: %d/s | Read: %d/s | Modify: %d/s | Errors: %d%n", seconds, w, r, m, e);
        }, 1, 1, TimeUnit.SECONDS);

        CountDownLatch latch = new CountDownLatch(parallelism);

        for (int i = 0; i < parallelism; i++) {
            final int taskNum = i;
            pool.execute(() -> {
                try {
                    for (int j = 0; j < operationsPerTask; j++) {
                        String playerName = "player_" + taskNum + "_" + j;
                        UUID uuid = UUID.randomUUID();
                        trackedPlayers.put(playerName, uuid);

                        PlayerData playerData = new PlayerData(uuid, playerName, 0, 0);
                        cantData.dataLogicMaintainer().writeOnly(PlayerData.class, playerData);
                        writeOps.increment();

                        int kills = cantData.dataLogicMaintainer().readOnly(PlayerData.class, playerName, PlayerData::kills);
                        readOps.increment();

                        cantData.dataLogicMaintainer().modify(PlayerData.class, playerName, p -> p.kills(kills + 1));
                        modifyOps.increment();

                        int newKills = cantData.dataLogicMaintainer().readOnly(PlayerData.class, playerName, PlayerData::kills);
                        readOps.increment();

                        if (newKills != kills + 1) {
                            errors.increment();
                        }
                    }
                } catch (Exception e) {
                    errors.increment();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Thread.sleep(2000);

        System.out.println("Verifica dati dopo delay...");

        for (String playerName : trackedPlayers.keySet()) {
            try {
                int kills = cantData.dataLogicMaintainer().readOnly(PlayerData.class, playerName, PlayerData::kills);
                readOps.increment();
                if (kills < 1) {
                    System.err.println("Incoerenza su: " + playerName + " (kills=" + kills + ")");
                    errors.increment();
                }
            } catch (Exception e) {
                errors.increment();
            }
        }

        monitor.shutdownNow();
        pool.shutdown();

        System.out.println("Stress test completato.");
        System.out.println("Totale Write: " + writeOps.sum());
        System.out.println("Totale Read: " + readOps.sum());
        System.out.println("Totale Modify: " + modifyOps.sum());
        System.out.println("Totale Errori: " + errors.sum());
    }
}
