package cc.seati.SeatiCore.Commands.Modules.Stats;

import cc.seati.SeatiCore.Commands.Abstract.Command;
import cc.seati.SeatiCore.Utils.ConfigUtil;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public class CommandReload extends Command {

    @Override
    public int handle(CommandContext<CommandSourceStack> ctx) {
        ConfigUtil.reload();
        ctx.getSource().sendSystemMessage(Component.literal("Reloaded configuration file."));
        return 1;
    }
}
