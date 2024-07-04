package cc.seati.PlayerStats.Tracker;

import cc.carm.lib.easysql.api.SQLManager;
import cc.seati.PlayerStats.Config;
import cc.seati.PlayerStats.Database.Model.PlaytimeRecord;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class PlaytimeTracker {
    private final ServerPlayer targetPlayer;
    private final ScheduledExecutorService timerExecutor = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService afkExecutor = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService saveExecutor = Executors.newSingleThreadScheduledExecutor();
    private boolean isAFK = false;
    private boolean afkMessageReady = false;
    private int afkBufferTime = 0;
    private Vector<Double> playerPositionData = new Vector<>(4);
    private final PlaytimeRecord record;

    public PlaytimeTracker(ServerPlayer forPlayer) {
        this.targetPlayer = forPlayer;
        this.manager = manager;
        try {
            this.record = PlaytimeRecord.from(manager, Utils.getPeriodTag(), forPlayer.getName().getString()).get(5, TimeUnit.SECONDS);
        } catch (TimeoutException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动一个关于 forPlayer 玩家的游玩时间跟踪器，该跟踪器会
     * <ul>
     *     <li>每秒钟将<b>总在线时长</b>（total）、<b>挂机时长</b>（afk）同步到数据库中</li>
     *     <li>每秒钟增加<b>总在线时长</b>一秒，如果处于挂机状态，则也增加<b>挂机时长</b></li>
     *     <li>每秒钟检查玩家的<b>位置</b>（XYZ）和<b>眼位</b>（eye position）是否发生变动，如果任意一个参数未变，增加挂机缓冲值，
     *     当挂机缓冲值达到 config 中规定的阈值时，将挂机状态设置为 true，并选择是提示所有人该玩家 AFK 的消息，或是踢出游戏。</li>
     * </ul>
     * @param manager SQLManager
     */
    public void run(SQLManager manager) {
        // Save record at one second period to ensure preciseness.
        // Assuming the updating action will cost far less than one second.
        // Assuming that HikariCP will handle this relatively high concurrency scenario well.

        saveExecutor.scheduleAtFixedRate(() -> {
            // SYNC
            this.record.saveSync(manager);
        }, 0, 1, TimeUnit.SECONDS);

        timerExecutor.scheduleAtFixedRate(() -> {
            this.record.increaseTotal();
            if (this.isAFK) {
                this.record.increaseAfk();
            }
        }, 0, 1, TimeUnit.SECONDS);

        afkExecutor.scheduleAtFixedRate(() -> {
            Vec3 lookAngle = this.targetPlayer.getLookAngle();
            Vector<Double> newData = new Vector<>(List.of(this.targetPlayer.getX(), this.targetPlayer.getY(), this.targetPlayer.getZ(), lookAngle.x(), lookAngle.y(), lookAngle.z()));


            // Compare new data with old data.
            // The fact that any item of the data keeps unchanged suggests the player is not moving validly.
            if ((
                    playerPositionData.get(0).equals(newData.get(0))
                            || playerPositionData.get(2).equals(newData.get(2))
                            || playerPositionData.get(3).equals(newData.get(3))
                            || playerPositionData.get(4).equals(newData.get(4))
                            || playerPositionData.get(5).equals(newData.get(5))
            ) && (
                    playerPositionData.get(1).equals(newData.get(1))
            )) {
                // If so, add afkBufferTime by 1, makes it closer to the threshold.
                this.afkBufferTime += 1;
            } else {
                // If player is currently moving, reset buffer time and remove afk status.
                this.isAFK = false;
                this.afkMessageReady = true;
                this.afkBufferTime = 0;
            }

            playerPositionData = newData;

            // If buffer time exceeds the threshold of kicking, just disconnect the player with a reason.
            if (this.afkBufferTime >= Config.t.getInt("playtime.afk-kick-threshold", 3600)) {
                this.targetPlayer.connection.disconnect(Component.literal("You have been in AFK state for too long (over " + Config.t.getInt("playtime.afk-kick-threshold", 3600) + " seconds!)"));
                this.shutdown();
            }

            // If buffer time exceeds the threshold of notification, make isAFK true
            // The afk time will start to increase.
            // And a message will be broadcast.
            if (this.afkBufferTime >= Config.t.getInt("playtime.afk-notify-threshold", 300)) {
                this.isAFK = true;

                // Broadcast AFK message
                // Note: PlayerList#broadcastChatMessage is not suitable for this case.
                if (this.afkMessageReady) Utils.sendAll(targetPlayer.getServer(), getAFKMessageComponent(targetPlayer));
                this.afkMessageReady = false;
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public static MutableComponent getAFKMessageComponent(Player targetPlayer) {
        String pattern = Config.t.getString("playtime.afk-message-pattern", "$player is now afk.");
        return Component.literal(
                pattern.replaceAll("\\$player", targetPlayer.getName().getString())
        ).setStyle(Style.EMPTY.withColor(
                TextColor.fromLegacyFormat(ChatFormatting.GRAY)
        ));
    }

    public void shutdown() {
        this.timerExecutor.shutdown();
        this.afkExecutor.shutdown();
        this.saveExecutor.shutdown();
    }
}
