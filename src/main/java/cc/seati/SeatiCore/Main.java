package cc.seati.SeatiCore;

import cc.seati.SeatiCore.Database.Database;
import cc.seati.SeatiCore.Tracker.PlayersOnlineTracker;
import cc.seati.SeatiCore.WebSocket.WebSocketServer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Main.MOD_ID)
public final class Main {
    public static final String MOD_ID = "seaticore";
    public static final Logger LOGGER = LogManager.getLogger("SeatiCore");
    public static LocalData config;
    public static Database database;
    public static LocalData ranks;
    public static MinecraftServer server;
    public static PlayersOnlineTracker playersOnlineTracker;
    public static WebSocketServer wsServer;
    public static Thread wsThread;

    public Main() {
        DistExecutor.safeRunWhenOn(Dist.DEDICATED_SERVER, () -> () -> {
            LOGGER.info("Initializing SeatiCore...");
            config = new LocalData("seati");
            LOGGER.info("Initialized {}.yml", config.getFilename());
            ranks = new LocalData("seati-ranks");
            LOGGER.info("Initialized {}.yml", ranks.getFilename());
            database = new Database();
        });
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> () -> {
            LOGGER.warn("Invalid dist. This mod can only be enabled on server side.");
        });
    }
}
