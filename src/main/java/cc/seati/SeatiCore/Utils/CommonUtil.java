package cc.seati.SeatiCore.Utils;

import cc.seati.SeatiCore.Main;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.Function;

public class CommonUtil {
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

    public static void sendMessage(CommandContext<CommandSourceStack> ctx, String message) {
        ctx.getSource().sendSystemMessage(TextUtil.literal(message));
    }

    public static <T> T waitFor(Future<T> future) throws ExecutionException, InterruptedException, TimeoutException {
        return waitFor(future, 5);
    }

    public static <T> T waitFor(Future<T> future, int timeout) throws ExecutionException, InterruptedException, TimeoutException {
        return future.get(timeout, TimeUnit.SECONDS);
    }

    /**
     * 调用 toCall 并返回返回值，如果产生异常，则返回 returnOnException。异常的唯一处理是 printStackTrace。
     * @param toCall Lambda
     * @param returnOnException 当产生异常时的返回值
     * @return 原函数的返回值或异常的返回值
     * @param <T> 原函数或异常的返回值类型
     */
    public static <T> T tryReturn(Callable<T> toCall, T returnOnException) {
        return tryRun(toCall, e -> returnOnException);
    }

    public static <T> T tryRun(Callable<T> toCall, Function<Exception, T> runOnException) {
        try {
            return toCall.call();
        } catch (Exception e) {
            e.printStackTrace();
            Main.LOGGER.warn("Caught exception");
            return runOnException.apply(e);
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

    public static String getColoredIndex(int index) {
        return switch (index) {
            case 1 -> "&b";
            case 2 -> "&a";
            case 3 -> "&e";
            default -> "&f";
        } + index + "&r";
    }

    public static boolean runCommand(String command) {
        return CommonUtil.tryRun(() -> {
            Main.server.getCommands().getDispatcher().execute(command, Main.server.createCommandSourceStack());
            return true;
        }, e -> {
            Main.LOGGER.warn("Cannot execute command: " + command);
            e.printStackTrace();
            return false;
        });
    }

    public static boolean saveEverything(MinecraftServer server) {
        return server.saveEverything(true, true, true);
    }

    public static @Nullable Process runScript(String scriptPath) {
        ProcessBuilder pb = new ProcessBuilder(scriptPath);
        pb.directory(new File("/"));
        return tryReturn(pb::start, null);
    }

    public static CompletableFuture<Void> runScriptAndWait(String scriptPath) {
        Process p = runScript(scriptPath);

        return CompletableFuture.runAsync(() -> {
            if (p == null) {
                return;
            }

            try {
                p.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
