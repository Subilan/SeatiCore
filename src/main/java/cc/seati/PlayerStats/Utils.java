package cc.seati.PlayerStats;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.*;

public class Utils {
    public static ServerPlayer getServerPlayer(Player p) {
        return Objects.requireNonNull(p.getServer()).getPlayerList().getPlayer(p.getUUID());
    }

    public static Timestamp getCurrentTimestamp() {
        return new Timestamp(new Date().getTime());
    }

    public static String formatTimestamp(Timestamp timestamp) {
        return timestamp.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
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
        ctx.getSource().sendSystemMessage(Text.literal(message));
    }

    public static <T> T waitFor(Future<T> future) throws ExecutionException, InterruptedException, TimeoutException {
        return future.get(5, TimeUnit.SECONDS);
    }

    /**
     * 调用 toCall 并返回返回值，如果产生异常，则返回 returnOnException。异常的唯一处理是 printStackTrace。
     * @param toCall Lambda
     * @param returnOnException 当产生异常时的返回值
     * @return 原函数的返回值或异常的返回值
     * @param <T> 原函数或异常的返回值类型
     */
    public static <T> T tryReturn(Callable<T> toCall, T returnOnException) {
        try {
            return toCall.call();
        } catch (Exception e) {
            e.printStackTrace();
            Main.LOGGER.warn("Caught exception.");
            return returnOnException;
        }
    }

    public static void tryExec(Callable<Void> toCall) {
        try {
            toCall.call();
        } catch (Exception e) {
            Main.LOGGER.warn("Caught exception.");
            e.printStackTrace();
        }
    }
}
