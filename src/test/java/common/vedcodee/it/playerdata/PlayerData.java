package common.vedcodee.it.playerdata;

import dev.vedcodee.it.cantdata.data.DataObject;
import dev.vedcodee.it.cantdata.type.Type;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.*;

@Getter @Setter
@Accessors(fluent = true)
public class PlayerData extends DataObject {

    private final UUID uuid;
    private String name;
    private int kills;
    private int deaths;

    public PlayerData(UUID uuid, String name, int kills, int deaths) {
        this.uuid = uuid;
        this.name = name;
        this.kills = kills;
        this.deaths = deaths;
    }


    @Override
    public List<String> keys() {
        return new ArrayList<>(Arrays.asList(uuid.toString(), name));
    }
}
