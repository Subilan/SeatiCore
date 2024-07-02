package cc.seati;

import cc.seati.Database.Database;
import dev.architectury.event.events.common.PlayerEvent;

import java.util.logging.LogManager;
import java.util.logging.Logger;

public final class Main {
    public static final String MOD_ID = "playerdatatracer";
    public static final Logger LOGGER = LogManager.getLogManager().getLogger("PlayerDataTracer");

    public static void init() {
        Config.init();
        Database.init();

        PlayerEvent.PLAYER_JOIN.register(Events::handlePlayerJoin);
        PlayerEvent.PLAYER_QUIT.register(Events::handlePlayerQuit);
    }
}
