package cc.seati.PlayerStats;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

    public static void sendMessageCtx(CommandContext<CommandSourceStack> ctx, String message) {
        ctx.getSource().sendSystemMessage(Component.literal(message));
    }

    public static <T> T waitFor(Future<T> future) throws ExecutionException, InterruptedException, TimeoutException {
        return future.get(5, TimeUnit.SECONDS);
    }
}
