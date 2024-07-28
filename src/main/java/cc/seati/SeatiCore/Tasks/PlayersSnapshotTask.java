package cc.seati.SeatiCore.Tasks;

import cc.seati.SeatiCore.Database.Model.OnlinePlayerSnapshot;
import cc.seati.SeatiCore.Utils.DBUtil;
import net.minecraft.server.MinecraftServer;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PlayersSnapshotTask {
    private final ScheduledExecutorService snapshotExecutor = Executors.newSingleThreadScheduledExecutor();
    private final int interval;
    private final MinecraftServer server;

    public PlayersSnapshotTask(int interval, MinecraftServer server) {
        this.interval = interval;
        this.server = server;
    }

    public void run() {
        snapshotExecutor.scheduleAtFixedRate(() -> {
            List<String> playerNames = server.getPlayerList().getPlayers().stream().map(p -> p.getName().getString()).toList();
            new OnlinePlayerSnapshot(playerNames.size(), String.join(",", playerNames)).saveAsync(DBUtil.getManager());
        }, 0, this.interval, TimeUnit.SECONDS);
    }

    public void shutdown() {
        snapshotExecutor.shutdown();
    }
}
