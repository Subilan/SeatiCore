package cc.seati.SeatiCore.Commands.Modules.Lab;

import cc.seati.SeatiCore.Commands.Abstract.Command;
import cc.seati.SeatiCore.Utils.CommonUtil;
import cc.seati.SeatiCore.Utils.ConfigUtil;
import cc.seati.SeatiCore.Utils.OSSUtil;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;

public class CommandArchive extends Command {
    @Override
    public int handle(CommandContext<CommandSourceStack> ctx) {
        CommonUtil.sendMessage(ctx, "&e开始归档，服务器线程在此过程将被阻塞至多 &c" + ConfigUtil.getOssUploadTimeout() + "s");
        OSSUtil.doArchive();
        CommonUtil.sendMessage(ctx, "&e执行完毕");
        return 1;
    }
}
