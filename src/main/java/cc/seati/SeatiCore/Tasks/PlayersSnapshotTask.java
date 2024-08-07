package cc.seati.SeatiCore.Tasks;

import cc.seati.SeatiCore.Database.Model.OnlinePlayerSnapshot;
import cc.seati.SeatiCore.Main;
import cc.seati.SeatiCore.Utils.DBUtil;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PlayersSnapshotTask extends Task {
    private final int interval;
    private final MinecraftServer server;
    private @Nullable LocalDateTime lastExecution = null;

    public PlayersSnapshotTask(int interval, MinecraftServer server) {
        this.interval = interval;
        this.server = server;
    }

    public void start() {
        Main.LOGGER.info("Running PlayersSnapshotTask at duration of {}s", this.interval);
        taskFuture = executorService.scheduleAtFixedRate(() -> {
            List<String> playerNames = server.getPlayerList().getPlayers().stream().map(p -> p.getName().getString()).toList();
            new OnlinePlayerSnapshot(playerNames.size(), String.join(",", playerNames)).saveAsync(DBUtil.getManager());
            lastExecution = LocalDateTime.now();
        }, 0, this.interval, TimeUnit.SECONDS);
    }

    @Override
    public int getInterval() {
        return this.interval;
    }

    @Override
    public @Nullable LocalDateTime getLastExecution() {
        return lastExecution;
    }

    @Override
    public TaskType getType() {
        return TaskType.PLAYER_SNAPSHOT;
    }
}
