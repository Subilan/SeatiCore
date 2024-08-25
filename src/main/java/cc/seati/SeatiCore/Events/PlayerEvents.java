package cc.seati.SeatiCore.Events;

import cc.seati.SeatiCore.Database.Model.Enums.LoginRecordActionType;
import cc.seati.SeatiCore.Database.Model.LoginRecord;
import cc.seati.SeatiCore.Main;
import cc.seati.SeatiCore.Tasks.TrackPlaytimeTask;
import cc.seati.SeatiCore.Utils.*;
import cc.seati.SeatiCore.Utils.Records.MCIDUsage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(Dist.DEDICATED_SERVER)
public class PlayerEvents {
    public static Map<ServerPlayer, TrackPlaytimeTask> playtimeTrackerMap = new HashMap<>();

    @SubscribeEvent
    public static void handlePlayerLogin(PlayerEvent.PlayerLoggedInEvent e) {
        ServerPlayer player = CommonUtil.getServerPlayer(e.getEntity());
        TrackPlaytimeTask tracker = new TrackPlaytimeTask(player, DBUtil.getManager());
        tracker.run();
        playtimeTrackerMap.put(player, tracker);
        Main.LOGGER.info("Starting playtime tracker for player {}", player.getName().getString());

        new LoginRecord(
                LoginRecordActionType.LOGIN,
                player.getName().getString(),
                player.getStringUUID(),
                ConfigUtil.getPeriodTag(),
                // Check if is first login in this period tag
                LoginRecord.isFirstLogin(DBUtil.getManager(), player.getName().getString(), ConfigUtil.getPeriodTag())
        ).saveAsync(DBUtil.getManager());

        Main.LOGGER.info("Checking player name binding state...");
        @Nullable MCIDUsage usage = CommonUtil.tryReturn(() -> CommonUtil.waitFor(LabUtil.getMCIDUsage(player.getName().getString())), null);
        if (usage == null) {
            Main.LOGGER.warn("Could not check player name binding state.");
        } else {
            Main.LOGGER.info("Check completed: used={}, verified={}", usage.used(), usage.verified());
            if (usage.used() && !usage.verified()) {
                player.sendSystemMessage(TextUtil.literal("[&b提示&f] 你有未验证的&a绑定请求&f，请输入 &e/seati lab verify&f 完成绑定。目标 Lab 用户名：&e" + usage.with()));
            }
        }
    }

    @SubscribeEvent
    public static void handlePlayerLogout(PlayerEvent.PlayerLoggedOutEvent e) {
        ServerPlayer player = CommonUtil.getServerPlayer(e.getEntity());
        playtimeTrackerMap.get(player).shutdown();
        playtimeTrackerMap.remove(player);
        Main.LOGGER.info("Shutting down playtime tracker for player {}", player.getName().getString());
        new LoginRecord(
                LoginRecordActionType.LOGOUT,
                player.getName().getString(),
                player.getStringUUID(),
                ConfigUtil.getPeriodTag(),
                false
        ).saveAsync(DBUtil.getManager());
    }

    @SubscribeEvent
    public static void handlePlayerInteract(PlayerInteractEvent e) {
        ServerPlayer player = CommonUtil.getServerPlayer(e.getEntity());
        TrackPlaytimeTask target = playtimeTrackerMap.get(player);
        if (target != null) target.clearAFKState();
    }

    @SubscribeEvent
    public static void handlePlayerContainer(PlayerContainerEvent e) {
        ServerPlayer player = CommonUtil.getServerPlayer(e.getEntity());
        TrackPlaytimeTask target = playtimeTrackerMap.get(player);
        if (target != null) target.clearAFKState();
    }
}
