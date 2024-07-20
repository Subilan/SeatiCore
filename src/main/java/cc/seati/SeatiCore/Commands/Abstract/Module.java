package cc.seati.SeatiCore.Commands.Abstract;

import cc.seati.SeatiCore.Utils.CommonUtil;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;

public abstract class Module {
    public abstract int handle(CommandContext<CommandSourceStack> ctx, String module, String action, String[] argArray) throws IllegalStateException;

    public int run(CommandContext<CommandSourceStack> ctx, String module, String[] argArray) {
        if (argArray.length == 0) {
            CommonUtil.sendMessage(ctx, "&cNot enough argument.");
            return 0;
        }

        try {
            return handle(ctx, module, argArray[0], argArray);
        } catch (IllegalStateException e) {
            CommonUtil.sendMessage(ctx, "&cNo handler set for " + argArray[0] + "." + module + "");
            return 1;
        }
    }
}
