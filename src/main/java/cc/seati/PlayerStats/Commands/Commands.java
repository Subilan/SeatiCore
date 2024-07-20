package cc.seati.PlayerStats.Commands;

import cc.seati.PlayerStats.Commands.Modules.Stats.*;
import cc.seati.PlayerStats.Commands.Modules.Lab.ModuleLab;
import cc.seati.PlayerStats.Utils.CommonUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Commands {
    public static final List<String> MODULES = List.of("stats", "lab");

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> command =
                net.minecraft.commands.Commands.literal("seati")
                        .requires(source -> source.hasPermission(2))
                        .then(
                                net.minecraft.commands.Commands.argument("module", StringArgumentType.string())
                                        .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(
                                                MODULES,
                                                builder
                                        ))
                                        .executes(ctx -> {
                                            CommonUtil.sendMessage(ctx, "&cNot enough argument.");
                                            return 1;
                                        })
                                        .then(
                                                net.minecraft.commands.Commands.argument("args...", StringArgumentType.greedyString())
                                                        .suggests(Commands::suggest)
                                                        .executes(Commands::handle)
                                        )
                        )
                        .executes(Commands::doSendVersionInformation);
        dispatcher.register(command);
    }

    public static CompletableFuture<Suggestions> suggest(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        String module = StringArgumentType.getString(ctx, "module");

        return SharedSuggestionProvider.suggest(
                switch (module) {
                    case "stats" -> ModuleStats.ACTIONS;
                    case "lab" -> ModuleLab.ACTIONS;
                    default -> List.of();
                },
                builder
        );
    }

    public static int handle(CommandContext<CommandSourceStack> ctx) {
        String args = StringArgumentType.getString(ctx, "args...");
        String module = StringArgumentType.getString(ctx, "module");

        String[] argArray = args.split(" ");

        return switch (module) {
            case "stats" -> new ModuleStats().run(ctx, module, argArray);

            case "lab" -> new ModuleLab().run(ctx, module, argArray);

            default -> {
                CommonUtil.sendMessage(ctx, "&cNo default handler set for module " + module + ".");
                yield 1;
            }
        };
    }

    public static int doSendVersionInformation(CommandContext<CommandSourceStack> ctx) {
        CommonUtil.sendMessage(ctx, "&bSeati &7| &eversion &a1.0");
        return 1;
    }
}
