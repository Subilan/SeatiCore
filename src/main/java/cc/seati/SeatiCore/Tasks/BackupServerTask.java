package cc.seati.SeatiCore.Tasks;

import cc.seati.SeatiCore.Main;
import cc.seati.SeatiCore.Utils.CommonUtil;
import cc.seati.SeatiCore.Utils.ConfigUtil;
import cc.seati.SeatiCore.Utils.OSSUtil;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class BackupServerTask extends Task {
    private final MinecraftServer server;
    private @Nullable LocalDateTime lastExecution = null;

    public BackupServerTask(MinecraftServer server) {
        this.server = server;
    }

    public void start() {
        Main.LOGGER.info("Running BackupServerTask at interval of {}s", ConfigUtil.getOssBackupInterval());
        taskFuture = executorService.scheduleAtFixedRate(() -> {
            CommonUtil.saveEverything(server);
            Main.LOGGER.info("Uploading backup to OSS.");
            // "If any execution of this task takes longer than its period, then subsequent executions may start late, but will not concurrently execute."
            OSSUtil.doBackup();
            lastExecution = LocalDateTime.now();
        }, getInterval(), getInterval(), TimeUnit.SECONDS);
    }

    @Override
    public int getInterval() {
        return ConfigUtil.getOssBackupInterval();
    }

    @Override
    public @Nullable LocalDateTime getLastExecution() {
        return lastExecution;
    }

    @Override
    public TaskType getType() {
        return TaskType.BACKUP_SERVER;
    }
}
