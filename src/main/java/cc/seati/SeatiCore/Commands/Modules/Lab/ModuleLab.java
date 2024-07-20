package cc.seati.SeatiCore.Commands.Modules.Lab;

import cc.seati.SeatiCore.Commands.Abstract.Module;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;

import java.util.List;

public class ModuleLab extends Module {
    public static final List<String> ACTIONS = List.of("verify");

    @Override
    public int handle(CommandContext<CommandSourceStack> ctx, String module, String action, String[] argArray) {
        return switch (action) {
            case "verify" -> new CommandVerify().handle(ctx);
            default -> throw new IllegalStateException("Unexpected value: " + action);
        };
    }
}
