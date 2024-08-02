package cc.seati.SeatiCore.Tasks;

import cc.seati.SeatiCore.Main;
import cc.seati.SeatiCore.Utils.CommonUtil;
import cc.seati.SeatiCore.Utils.ConfigUtil;
import cc.seati.SeatiCore.Utils.OSSUtil;
import net.minecraft.server.MinecraftServer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BackupServerTask {
    private final ScheduledExecutorService backupServerExecutor = Executors.newSingleThreadScheduledExecutor();
    private final MinecraftServer server;

    public BackupServerTask(MinecraftServer server) {
        this.server = server;
    }

    public void run() {
        Main.LOGGER.info("Running BackupServerTask at interval of {}s", ConfigUtil.getOssBackupInterval());
        backupServerExecutor.scheduleAtFixedRate(() -> {
            CommonUtil.saveEverything(server);
            Main.LOGGER.info("Uploading backup to OSS.");
            // "If any execution of this task takes longer than its period, then subsequent executions may start late, but will not concurrently execute."
            OSSUtil.doBackup();
        }, 0, ConfigUtil.getOssBackupInterval(), TimeUnit.SECONDS);
    }

    public void shutdown() {
        backupServerExecutor.shutdown();
    }
}
