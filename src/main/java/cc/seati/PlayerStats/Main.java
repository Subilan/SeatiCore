package cc.seati.PlayerStats;

import cc.seati.PlayerStats.Database.Database;
import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;


@Mod("playerstats")
public final class Main {
    public static final String MOD_ID = "playerstats";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static void init() {
        DistExecutor.safeRunWhenOn(Dist.DEDICATED_SERVER, () -> () -> {
            LOGGER.info("Initializing PlayerStats...");
            Config.init();
            Database.init();
            LOGGER.info("Initialization end.");
        });
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> () -> {
            LOGGER.warn("Invalid dist. This mod can only be enabled on server side.");
        });
    }
}
