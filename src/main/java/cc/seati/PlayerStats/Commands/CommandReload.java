package cc.seati.PlayerStats.Commands;

import cc.seati.PlayerStats.Config;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public class CommandReload extends Command {

    @Override
    public int handle(CommandContext<CommandSourceStack> ctx) {
        Config.init(true);
        ctx.getSource().sendSystemMessage(Component.literal("Reloaded configuration file."));
        return 1;
    }
}
