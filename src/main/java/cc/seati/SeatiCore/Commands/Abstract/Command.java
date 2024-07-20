package cc.seati.SeatiCore.Commands.Abstract;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;

public abstract class Command {
    public abstract int handle(CommandContext<CommandSourceStack> ctx);
}
