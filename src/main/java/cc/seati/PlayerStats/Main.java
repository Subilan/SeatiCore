package cc.seati.PlayerStats;

import cc.seati.PlayerStats.Database.Database;
import net.minecraftforge.fml.common.Mod;

import java.util.logging.LogManager;
import java.util.logging.Logger;


@Mod("playerstats")
public final class Main {
    public static final String MOD_ID = "playerstats";
    public static final Logger LOGGER = LogManager.getLogManager().getLogger("PlayerStats");

    public static void init() {
        Config.init();
        Database.init();
    }
}
