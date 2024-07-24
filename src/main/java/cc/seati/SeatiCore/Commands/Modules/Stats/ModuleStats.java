package cc.seati.SeatiCore.Commands.Modules.Stats;

import cc.seati.SeatiCore.Commands.Abstract.Module;
import cc.seati.SeatiCore.Utils.CommonUtil;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;

import java.util.List;

public class ModuleStats extends Module {
    public static final List<String> ACTIONS = List.of("info", "board", "migrate");

    @Override
    public int handle(CommandContext<CommandSourceStack> ctx, String module, String action, String[] argArray) {
        return switch (action) {
            case "info" -> new CommandInfo(argArray.length == 1 ? "" : argArray[1]).handle(ctx);

            case "board" -> CommonUtil.tryRun(
                    () -> new CommandBoard(argArray.length >= 2 ? argArray[1] : "", argArray.length >= 3 ? Integer.parseInt(argArray[2]) : 1).handle(ctx),
                    e -> {
                        if (e instanceof NumberFormatException) {
                            CommonUtil.sendMessage(ctx, "&c页码必须是数字");
                            return 1;
                        }
                        return 0;
                    }
            );

            case "migrate" -> {
                if (argArray.length < 4) {
                    CommonUtil.sendMessage(ctx, "&c参数不足");
                    yield 1;
                }
                yield new CommandMigrate(argArray[1], argArray[2], argArray[3]).handle(ctx);
            }

            default -> throw new IllegalStateException("Unexpected value: " + action);
        };
    }
}
