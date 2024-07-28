package cc.seati.SeatiCore;

import cc.seati.SeatiCore.Database.Database;
import cc.seati.SeatiCore.Tasks.EmptyServerTask;
import cc.seati.SeatiCore.Tasks.PlayersSnapshotTask;
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
    public static PlayersSnapshotTask playersSnapshotTask;
    public static EmptyServerTask emptyServerTask;
    public static WebSocketServer wsServer;
    public static Thread wsThread;

    public Main() {
        DistExecutor.safeRunWhenOn(Dist.DEDICATED_SERVER, () -> () -> {
            LOGGER.info("Initializing SeatiCore...");
            LOGGER.info("Initializing seati.yml...");
            config = new LocalData("seati");
            LOGGER.info("Initializing seati-ranks.yml...");
            ranks = new LocalData("seati-ranks");
            LOGGER.info("Initializing database...");
            database = new Database();
        });
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> () -> {
            LOGGER.warn("Invalid dist. This mod can only be enabled on server side.");
        });
    }
}
