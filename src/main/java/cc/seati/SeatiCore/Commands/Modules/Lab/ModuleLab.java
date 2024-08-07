package cc.seati.SeatiCore.Commands.Modules.Lab;

import cc.seati.SeatiCore.Commands.Abstract.Module;
import cc.seati.SeatiCore.Utils.CommonUtil;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;

import java.util.List;

public class ModuleLab extends Module {
    public static final List<String> ACTIONS = List.of("verify", "archive", "backup", "taskinfo", "taskrun", "taskstop");

    @Override
    public int handle(CommandContext<CommandSourceStack> ctx, String module, String action, String[] argArray) {
        return switch (action) {
            case "verify" -> new CommandVerify().handle(ctx);
            case "archive" -> new CommandArchive().handle(ctx);
            case "backup" -> new CommandBackup().handle(ctx);
            case "taskinfo", "taskrun", "taskstop" -> {
                if (argArray.length < 2) {
                    CommonUtil.sendMessage(ctx, "&c参数不足");
                    yield 1;
                } else {
                    yield switch (action) {
                        case "taskinfo" -> new CommandTaskInfo(argArray[1]).handle(ctx);
                        case "taskrun" -> new CommandTaskRunStop(argArray[1], true).handle(ctx);
                        case "taskstop" -> new CommandTaskRunStop(argArray[1], false).handle(ctx);
                        default -> throw new IllegalStateException("Unexpected value: " + action);
                    };
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + action);
        };
    }
}
