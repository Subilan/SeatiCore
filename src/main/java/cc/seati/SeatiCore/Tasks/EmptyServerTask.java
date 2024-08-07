package cc.seati.SeatiCore.Tasks;

import cc.seati.SeatiCore.Main;
import cc.seati.SeatiCore.Utils.CommonUtil;
import cc.seati.SeatiCore.Utils.ConfigUtil;
import cc.seati.SeatiCore.Utils.LabUtil;
import cc.seati.SeatiCore.Utils.OSSUtil;
import net.minecraft.server.MinecraftServer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EmptyServerTask extends Task {
    private final MinecraftServer server;
    private int emptyTime = 0;

    public EmptyServerTask(MinecraftServer server) {
        this.server = server;
    }

    public void run() {
        Main.LOGGER.info("Running EmptyServerTask at interval of 1s, maxemptytime={}s", ConfigUtil.getMaxEmptyTime());
        executorService.scheduleAtFixedRate(() -> {
            if (server.getPlayerCount() == 0) {
                emptyTime += 1;
            } else {
                emptyTime = 0;
            }

            if (emptyTime > ConfigUtil.getMaxEmptyTime()) {
                Main.LOGGER.warn("Empty time reached the limit of {}s. Archiving files.", ConfigUtil.getMaxEmptyTime());
                CommonUtil.saveEverything(server);
                // "If any execution of this task takes longer than its period, then subsequent executions may start late, but will not concurrently execute."
                OSSUtil.doArchive();
                CommonUtil.waitForWhatever(LabUtil.deleteThis());
                return;
            }

            if (ConfigUtil.getMaxEmptyTime() - emptyTime <= 30) {
                Main.LOGGER.warn("The server will be archived and closed in {}s", ConfigUtil.getMaxEmptyTime() - emptyTime);
            }

        }, 0, 1, TimeUnit.SECONDS);
    }
}
