package cc.seati.PlayerStats;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

public class Utils {
    public static ServerPlayer getServerPlayer(Player p) {
        return Objects.requireNonNull(p.getServer()).getPlayerList().getPlayer(p.getUUID());
    }
}
