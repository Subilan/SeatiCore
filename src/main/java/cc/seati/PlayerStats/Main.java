package cc.seati.PlayerStats;

import cc.seati.PlayerStats.Database.Database;
import cc.seati.PlayerStats.Tracker.PlayersOnlineTracker;
import cc.seati.PlayerStats.WebSocket.WebSocketServer;
import net.minecraft.server.MinecraftServer;
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
    public static LocalData ranks;
    public static MinecraftServer server;
    public static PlayersOnlineTracker playersOnlineTracker;
    public static WebSocketServer wsServer;
    public static Thread wsThread;

    public Main() {
        DistExecutor.safeRunWhenOn(Dist.DEDICATED_SERVER, () -> () -> {
            LOGGER.info("Initializing PlayerStats...");
            config = new LocalData("playerstats");
            ranks = new LocalData("playerstats-ranks");
            database = new Database();
        });
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> () -> {
            LOGGER.warn("Invalid dist. This mod can only be enabled on server side.");
        });
    }
}
