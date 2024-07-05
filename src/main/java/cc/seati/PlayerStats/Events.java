package cc.seati.PlayerStats;

import cc.seati.PlayerStats.Commands.CommandManager;
import cc.seati.PlayerStats.Tracker.PlaytimeTracker;
import cc.seati.PlayerStats.Database.Database;
import cc.seati.PlayerStats.Database.Model.LoginRecord;
import cc.seati.PlayerStats.Database.Model.LoginRecordActionType;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(Dist.DEDICATED_SERVER)
public class Events {
    public static Map<ServerPlayer, PlaytimeTracker> playtimeTracerMap = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent e) {
        ServerPlayer player = Utils.getServerPlayer(e.getEntity());
        PlaytimeTracker tracer = new PlaytimeTracker(player, Database.manager);
        tracer.run();
        playtimeTracerMap.put(player, tracer);
        Main.LOGGER.info("Starting playtime tracer for player " + player.getName().getString());
        new LoginRecord(LoginRecordActionType.LOGIN, player.getName().getString()).saveAsync(Database.manager);
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent e) {
        ServerPlayer player = Utils.getServerPlayer(e.getEntity());
        playtimeTracerMap.get(player).shutdown();
        playtimeTracerMap.remove(player);
        Main.LOGGER.info("Shutting down playtime tracer for player " + player.getName().getString());
        new LoginRecord(LoginRecordActionType.LOGOUT, player.getName().getString()).saveAsync(Database.manager);
    }

    @SubscribeEvent
    public static void onRegisterCommand(RegisterCommandsEvent e) {
        CommandDispatcher<CommandSourceStack> dispatcher = e.getDispatcher();
        CommandManager.register(dispatcher);
    }
}
