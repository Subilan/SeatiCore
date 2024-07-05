package cc.seati.PlayerStats;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;

public class Utils {
    public static ServerPlayer getServerPlayer(Player p) {
        return Objects.requireNonNull(p.getServer()).getPlayerList().getPlayer(p.getUUID());
    }

    public static Timestamp getCurrentTimestamp() {
        return new Timestamp(new Date().getTime());
    }

    public static void sendAll(MinecraftServer server, Component component) {
        Objects.requireNonNull(server)
                .getPlayerList()
                .getPlayers()
                .forEach(
                        p -> p.sendSystemMessage(component)
                );
    }
}
