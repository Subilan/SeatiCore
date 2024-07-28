package cc.seati.SeatiCore.Tasks;

import cc.carm.lib.easysql.api.SQLManager;
import cc.seati.SeatiCore.Database.Model.PlaytimeRecord;
import cc.seati.SeatiCore.Main;
import cc.seati.SeatiCore.Utils.CommonUtil;
import cc.seati.SeatiCore.Utils.ConfigUtil;
import cc.seati.SeatiCore.Utils.RankUtil;
import cc.seati.SeatiCore.Utils.TextUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class TrackPlaytimeTask {
    private final ServerPlayer targetPlayer;
    private final String targetPlayerName;
    private final ScheduledExecutorService timerExecutor = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService afkExecutor = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService saveExecutor = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService rankExecutor = Executors.newSingleThreadScheduledExecutor();
    private boolean isAFK = false;
    private boolean afkMessageFlag = true;
    private int afkBufferTime = 0;
    private Vector<Double> playerPositionData = new Vector<>(List.of(0d, 0d, 0d, 0d, 0d, 0d));
    private final @Nullable PlaytimeRecord record;
    private final SQLManager manager;

    public TrackPlaytimeTask(ServerPlayer forPlayer, SQLManager manager) {
        this.targetPlayer = forPlayer;
        this.targetPlayerName = forPlayer.getName().getString();
        this.manager = manager;
        this.record = CommonUtil.tryReturn(() -> CommonUtil.waitFor(PlaytimeRecord.from(manager, ConfigUtil.getPeriodTag(), forPlayer.getName().getString(), forPlayer.getStringUUID(), true)), null);
    }

    /**
     * 启动一个关于 forPlayer 玩家的游玩时间跟踪器，该跟踪器会
     * <ul>
     *     <li>每秒钟将<b>总在线时长</b>（total）、<b>挂机时长</b>（afk）同步到数据库中</li>
     *     <li>每秒钟增加<b>总在线时长</b>一秒，如果处于挂机状态，则也增加<b>挂机时长</b></li>
     *     <li>每秒钟检查玩家的<b>位置</b>（XYZ）和<b>眼位</b>（eye position）是否发生变动，如果任意一个参数未变，增加挂机缓冲值，
     *     当挂机缓冲值达到 config 中规定的阈值时，将挂机状态设置为 true，并选择是提示所有人该玩家 AFK 的消息，或是踢出游戏。</li>
     * </ul>
     */
    public void run() {

        if (this.record == null) {
            Main.LOGGER.warn("Run playtime tracker for " + targetPlayer.getName().getString() + " failed.");
            return;
        }

        // Save record at one-second period to ensure preciseness.
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
                if (!this.afkMessageFlag) CommonUtil.sendAll(targetPlayer.getServer(), getAfkLeavingMessageComponent(targetPlayer));
                this.afkMessageFlag = true;
                this.afkBufferTime = 0;
            }

            playerPositionData = newData;

            // If buffer time exceeds the threshold of kicking, just disconnect the player with a reason.
            if (this.afkBufferTime >= ConfigUtil.getAfkKickThreshold()) {
                this.targetPlayer.connection.disconnect(Component.literal("You have been kicked for idling more than " + ConfigUtil.getAfkKickThreshold() + " seconds."));
                this.shutdown();
            }

            // If buffer time exceeds the threshold of notification, make isAFK true
            // The afk time will start to increase.
            // And a message will be broadcast.
            if (this.afkBufferTime >= ConfigUtil.getAfkNotifyThreshold()) {
                this.isAFK = true;

                // Broadcast AFK message
                // Note: PlayerList#broadcastChatMessage is not suitable for this case.
                if (this.afkMessageFlag) CommonUtil.sendAll(targetPlayer.getServer(), getAfkEnteringMessageComponent(targetPlayer));
                this.afkMessageFlag = false;
            }
        }, 0, 1, TimeUnit.SECONDS);

        if (ConfigUtil.getEnableFTBRanksIntegration()) {
            Main.LOGGER.info("FTB Ranks integration is enabled. Starting rank executor for player.");
            rankExecutor.scheduleAtFixedRate(() -> {
                Map<String, Integer> mapRequirements = ConfigUtil.getRankRequirements();
                for (Map.Entry<String, Integer> entry : mapRequirements.entrySet()) {
                    if (record.getValidTime() >= entry.getValue()) if (!RankUtil.hasRank(this.targetPlayerName, entry.getKey())) {
                        if (RankUtil.setRank(this.targetPlayerName, entry.getKey(), true)) {
                            Main.LOGGER.info("Player {} playtime exceeds {}, adding rank {}.", this.targetPlayerName, TextUtil.formatSeconds(entry.getValue()), entry.getKey());
                            targetPlayer.sendSystemMessage(TextUtil.literal("&e你的&a有效游玩时间&e已经达到 &b" + TextUtil.formatSeconds(entry.getValue()) + "&e，获得权限组 &b" + entry.getKey() + "&e！"));
                        }
                    }
                }
            }, 0, 1, TimeUnit.SECONDS);
        }
    }

    public static MutableComponent getAfkEnteringMessageComponent(Player targetPlayer) {
        return TextUtil.literal("&7" + ConfigUtil.getAfkEnteringMessagePattern().replaceAll("\\$player", targetPlayer.getName().getString()));
    }

    public static MutableComponent getAfkLeavingMessageComponent(Player targetPlayer) {
        return TextUtil.literal("&7" + ConfigUtil.getAfkLeavingMessagePattern().replaceAll("\\$player", targetPlayer.getName().getString()));
    }

    public void shutdown() {
        this.timerExecutor.shutdown();
        this.afkExecutor.shutdown();
        this.saveExecutor.shutdown();
        if (ConfigUtil.getEnableFTBRanksIntegration()) {
            this.rankExecutor.shutdown();
        }
    }
}
