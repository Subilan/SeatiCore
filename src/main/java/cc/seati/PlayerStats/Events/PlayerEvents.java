package cc.seati.PlayerStats.Events;

import cc.seati.PlayerStats.Database.Model.Enums.LoginRecordActionType;
import cc.seati.PlayerStats.Database.Model.LoginRecord;
import cc.seati.PlayerStats.Main;
import cc.seati.PlayerStats.Tracker.PlaytimeTracker;
import cc.seati.PlayerStats.Utils.CommonUtil;
import cc.seati.PlayerStats.Utils.ConfigUtil;
import cc.seati.PlayerStats.Utils.DBUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(Dist.DEDICATED_SERVER)
public class PlayerEvents {
    public static Map<ServerPlayer, PlaytimeTracker> playtimeTracerMap = new HashMap<>();

    @SubscribeEvent
    public static void handlePlayerLogin(PlayerEvent.PlayerLoggedInEvent e) {
        ServerPlayer player = CommonUtil.getServerPlayer(e.getEntity());
        PlaytimeTracker tracer = new PlaytimeTracker(player, DBUtil.getManager());
        tracer.run();
        playtimeTracerMap.put(player, tracer);
        Main.LOGGER.info("Starting playtime tracker for player " + player.getName().getString());
        new LoginRecord(
                LoginRecordActionType.LOGIN,
                player.getName().getString(),
                ConfigUtil.getPeriodTag(),
                // Check if is first login in this period tag
                LoginRecord.isFirstLogin(DBUtil.getManager(), player.getName().getString(), ConfigUtil.getPeriodTag())
        ).saveAsync(DBUtil.getManager());
    }

    @SubscribeEvent
    public static void handlePlayerLogout(PlayerEvent.PlayerLoggedOutEvent e) {
        ServerPlayer player = CommonUtil.getServerPlayer(e.getEntity());
        playtimeTracerMap.get(player).shutdown();
        playtimeTracerMap.remove(player);
        Main.LOGGER.info("Shutting down playtime tracker for player " + player.getName().getString());
        new LoginRecord(
                LoginRecordActionType.LOGOUT,
                player.getName().getString(),
                ConfigUtil.getPeriodTag(),
                false
        ).saveAsync(DBUtil.getManager());
    }
}
