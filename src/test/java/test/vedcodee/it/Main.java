package test.vedcodee.it;

import common.vedcodee.it.database.MySQLConnection;
import common.vedcodee.it.database.RedisConnector;
import common.vedcodee.it.playerdata.PlayerData;
import common.vedcodee.it.playerdata.serializer.PlayerDataSerializer;
import dev.vedcodee.it.cantdata.CantDataBootstrap;
import dev.vedcodee.it.cantdata.type.Type;
import common.vedcodee.it.utils.Auth;

import java.util.UUID;

public class Main {

    public static void main(String[] args) {

        CantDataBootstrap cantData = new CantDataBootstrap();
        cantData.add(PlayerData.class, new PlayerDataSerializer(), Type.PERSISTENT);
        MySQLConnection mySQLConnection = new MySQLConnection(new Auth("test1", "localhost", 3306, "root", ""));
        RedisConnector redisConnector = new RedisConnector("127.0.0.1", 6379, "", "");
        cantData.setDatabase(Type.PERSISTENT, mySQLConnection);
        cantData.setDatabase(Type.CACHED, redisConnector);

        cantData.initialize();

        System.out.println(cantData.databases().get(Type.PERSISTENT).all(PlayerData.class.getName()).size());

        PlayerData playerData = new PlayerData(UUID.randomUUID(), "playerName", 1, 2);

        System.out.println("writing...");

        cantData.dataLogicMaintainer().writeOnly(PlayerData.class, playerData);

        int kills = cantData.dataLogicMaintainer().readOnly(PlayerData.class, "playerName", (reader) -> reader.kills());

        cantData.dataLogicMaintainer().modify(PlayerData.class, "playerName", (p) -> {
            p.kills(10);
        });

        kills = cantData.dataLogicMaintainer().readOnly(PlayerData.class, "playerName", (reader) -> reader.kills());

        System.out.println("kills: " + kills);
    }

}
