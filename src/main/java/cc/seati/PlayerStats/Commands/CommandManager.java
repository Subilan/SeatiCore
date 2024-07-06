package cc.seati.PlayerStats.Commands;

import cc.seati.PlayerStats.Utils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class CommandManager {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> command =
                Commands.literal("playerstats")
                        .requires(source -> source.hasPermission(2))
                        .then(
                                Commands.argument("input", StringArgumentType.greedyString())
                                        .executes(CommandManager::withActionArgumentHandler)
                        )
                        .executes(CommandManager::doSendVersionInformation);
        dispatcher.register(command);
    }

    public static int withActionArgumentHandler(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        String input = StringArgumentType.getString(ctx, "input");
        String[] inputs = input.split(" ");
        if (inputs.length == 0) {
            Utils.sendMessageCtx(ctx, "Not enough argument.");
            return 0;
        }

        String action = inputs[0];
        return switch (action) {
            case "reload" -> new CommandReload().handle(ctx);
            case "info" -> {
                if (inputs.length == 1) {
                    yield new CommandInfo().handle(ctx);
                }
                yield new CommandInfo(inputs[1]).handle(ctx);
            }
            case "board" -> Utils.tryRun(
                    () -> new CommandBoard(inputs.length >= 2 ? inputs[1] : "", inputs.length >= 3 ? Integer.parseInt(inputs[2]) : 1).handle(ctx),
                    e -> {
                        if (e instanceof NumberFormatException) {
                            Utils.sendMessageCtx(ctx, "&c页码必须是数字");
                            return 1;
                        }
                        return 0;
                    }
            );
            default -> {
                Utils.sendMessageCtx(ctx, "No handler set for parameter " + action + ".");
                yield 1;
            }
        };
    }

    public static int doSendVersionInformation(CommandContext<CommandSourceStack> ctx) {
        ctx.getSource().sendSystemMessage(
                Component.literal("Player").withStyle(ChatFormatting.AQUA).append(
                        Component.literal("Stats").withStyle(ChatFormatting.YELLOW).append(
                                Component.literal(" | version ").withStyle(ChatFormatting.GRAY).append(
                                        Component.literal("1.0")
                                )
                        )
                )
        );
        return 1;
    }
}
