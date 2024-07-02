package cc.seati;

import cc.seati.Database.Database;
import cc.seati.Database.Model.LoginRecord;
import cc.seati.Database.Model.LoginRecordActionType;
import net.minecraft.server.level.ServerPlayer;

public class Events {
    public static void handlePlayerJoin(ServerPlayer player) {
        new LoginRecord(LoginRecordActionType.LOGIN, player.getName().getString()).save(Database.manager);
    }

    public static void handlePlayerQuit(ServerPlayer player) {
        new LoginRecord(LoginRecordActionType.LOGOUT, player.getName().getString()).save(Database.manager);
    }
}
