package cc.seati.PlayerStats.Commands;

import cc.seati.PlayerStats.Utils.CommonUtil;
import cc.seati.PlayerStats.Utils.LabUtil;
import cc.seati.PlayerStats.Utils.Records.MCIDUsage;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class CommandVerify extends Command {

    @Override
    public int handle(CommandContext<CommandSourceStack> ctx) {
        if (!ctx.getSource().isPlayer()) {
            CommonUtil.sendMessage(ctx, "&cThis command can only be executed by player.");
            return 1;
        }

        String playername = Objects.requireNonNull(ctx.getSource().getPlayer()).getName().getString();

        @Nullable MCIDUsage usage = CommonUtil.tryReturn(() -> CommonUtil.waitFor(LabUtil.getMCIDUsage(playername)), null);

        if (usage == null) {
            CommonUtil.sendMessage(ctx, "&c无法获取绑定信息");
            return 1;
        }

        if (!usage.used()) {
            CommonUtil.sendMessage(ctx, "&e没有与 " + playername + " 相关的绑定请求");
            return 1;
        }

        if (usage.verified()) {
            CommonUtil.sendMessage(ctx, "&e已经与账号 " + usage.with() + " 绑定");
            return 1;
        }

        LabUtil.verifyMCID(playername).thenApply(s -> {
            if (s) {
                CommonUtil.sendMessage(ctx, "&a绑定成功");
            } else {
                CommonUtil.sendMessage(ctx, "&c绑定失败");
            }
            return null;
        });

        return 1;
    }
}
