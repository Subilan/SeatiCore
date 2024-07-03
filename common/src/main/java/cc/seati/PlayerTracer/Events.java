package cc.seati.PlayerTracer;

import cc.seati.PlayerTracer.Database.Database;
import cc.seati.PlayerTracer.Database.Model.LoginRecord;
import cc.seati.PlayerTracer.Database.Model.LoginRecordActionType;
import cc.seati.PlayerTracer.Tracer.PlaytimeTracer;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;

public class Events {
    public static Map<ServerPlayer, PlaytimeTracer> playtimeTracerMap = new HashMap<>();

    public static void handlePlayerJoin(ServerPlayer player) {
        PlaytimeTracer tracer = new PlaytimeTracer(player);
        tracer.run(Database.manager);
        playtimeTracerMap.put(player, tracer);
        Main.LOGGER.info("Starting playtime tracer for player " + player.getName().getString());
        new LoginRecord(LoginRecordActionType.LOGIN, player.getName().getString()).saveAsync(Database.manager);
    }

    public static void handlePlayerQuit(ServerPlayer player) {
        playtimeTracerMap.get(player).shutdown();
        playtimeTracerMap.remove(player);
        Main.LOGGER.info("Shutting down playtime tracer for player " + player.getName().getString());
        new LoginRecord(LoginRecordActionType.LOGOUT, player.getName().getString()).saveAsync(Database.manager);
    }
}
