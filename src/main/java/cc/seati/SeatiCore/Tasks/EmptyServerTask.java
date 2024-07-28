package cc.seati.SeatiCore.Tasks;

import cc.seati.SeatiCore.Main;
import cc.seati.SeatiCore.Utils.CommonUtil;
import cc.seati.SeatiCore.Utils.ConfigUtil;
import net.minecraft.server.MinecraftServer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EmptyServerTask {
    private final ScheduledExecutorService emptyServerExecutor = Executors.newSingleThreadScheduledExecutor();
    private final MinecraftServer server;
    private int emptyTime = 0;

    public EmptyServerTask(MinecraftServer server) {
        this.server = server;
    }

    public void run() {
        Main.LOGGER.info("Running EmptyServerTask at duration of 1s, maxemptytime={}s", ConfigUtil.getMaxEmptyTime());
        emptyServerExecutor.scheduleAtFixedRate(() -> {
            if (server.getPlayerCount() == 0) {
                emptyTime += 1;
            } else {
                emptyTime = 0;
            }

            if (emptyTime > ConfigUtil.getMaxEmptyTime()) {
                Main.LOGGER.warn("Empty time reached the limit of {}s. Closing server.", ConfigUtil.getMaxEmptyTime());
                // Do not use MinecraftServer#stopServer or MinecraftServer#close (which will cause exception thus incompletely closed hanging server process) in this scheduler.
                CommonUtil.runCommand("stop");
                return;
            }

            if (ConfigUtil.getMaxEmptyTime() - emptyTime <= 30) {
                Main.LOGGER.warn("The server will be closed in {}s", ConfigUtil.getMaxEmptyTime() - emptyTime);
            }

        }, 0, 1, TimeUnit.SECONDS);
    }

    public void shutdown() {
        emptyServerExecutor.shutdown();
    }
}
