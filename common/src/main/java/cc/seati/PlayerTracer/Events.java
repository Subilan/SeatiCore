package cc.seati.PlayerTracer;

import cc.seati.PlayerTracer.Database.Database;
import cc.seati.PlayerTracer.Database.Model.LoginRecord;
import cc.seati.PlayerTracer.Database.Model.LoginRecordActionType;
import net.minecraft.server.level.ServerPlayer;

public class Events {
    public static void handlePlayerJoin(ServerPlayer player) {
        new LoginRecord(LoginRecordActionType.LOGIN, player.getName().getString()).save(Database.manager);
        new LoginRecord(LoginRecordActionType.LOGIN, player.getName().getString()).saveSync(Database.manager);
    }

    public static void handlePlayerQuit(ServerPlayer player) {
        new LoginRecord(LoginRecordActionType.LOGOUT, player.getName().getString()).save(Database.manager);
        new LoginRecord(LoginRecordActionType.LOGOUT, player.getName().getString()).saveSync(Database.manager);
    }
}
