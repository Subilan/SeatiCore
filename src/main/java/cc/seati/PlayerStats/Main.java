package cc.seati.PlayerStats;

import cc.seati.PlayerStats.Database.Database;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Main.MOD_ID)
public final class Main {
    public static final String MOD_ID = "playerstats";
    public static final Logger LOGGER = LogManager.getLogger("PlayerStats");
    public static LocalData config;
    public static Database database;
    public Main() {
        DistExecutor.safeRunWhenOn(Dist.DEDICATED_SERVER, () -> () -> {
            LOGGER.info("Initializing PlayerStats...");
            config = new LocalData("playerstats");
            database = new Database();
        });
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> () -> {
            LOGGER.warn("Invalid dist. This mod can only be enabled on server side.");
        });
    }
}
