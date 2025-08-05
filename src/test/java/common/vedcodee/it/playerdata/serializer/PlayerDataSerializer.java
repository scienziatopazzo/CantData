package common.vedcodee.it.playerdata.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import common.vedcodee.it.playerdata.PlayerData;
import dev.vedcodee.it.cantdata.data.serializer.Manipulator;

import java.util.UUID;

public class PlayerDataSerializer implements Manipulator<PlayerData> {

    @Override
    public JsonElement toJson(PlayerData object) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uuid", object.uuid().toString());
        jsonObject.addProperty("name", object.name());
        jsonObject.addProperty("kills", object.kills());
        jsonObject.addProperty("deaths", object.deaths());

        return jsonObject;
    }

    @Override
    public PlayerData fromJson(JsonElement json) {

        JsonObject jsonObject = json.getAsJsonObject();
        PlayerData playerData = new PlayerData(
                UUID.fromString(jsonObject.get("uuid").getAsString()),
                jsonObject.get("name").getAsString(),
                jsonObject.get("kills").getAsInt(),
                jsonObject.get("deaths").getAsInt()
        );

        return playerData;
    }

}
