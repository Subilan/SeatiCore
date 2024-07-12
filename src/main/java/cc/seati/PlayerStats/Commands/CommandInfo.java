package cc.seati.PlayerStats.Commands;

import cc.seati.PlayerStats.Database.Model.LoginRecord;
import cc.seati.PlayerStats.Database.Model.PlaytimeRecord;
import cc.seati.PlayerStats.Utils.TextUtil;
import cc.seati.PlayerStats.Utils.CommonUtil;
import cc.seati.PlayerStats.Utils.ConfigUtil;
import cc.seati.PlayerStats.Utils.DBUtil;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;
import java.util.Objects;

public class CommandInfo extends Command {

    public final String targetPlayer;

    public CommandInfo(String targetPlayer) {
        this.targetPlayer = targetPlayer;
    }

    @Override
    public int handle(CommandContext<CommandSourceStack> ctx) {
        if (!ctx.getSource().isPlayer() && this.targetPlayer.isEmpty()) {
            CommonUtil.sendMessage(ctx, "This command can only be executed by player.");
            return 1;
        }

        String targetPlayerName = this.targetPlayer.isEmpty() ? Objects.requireNonNull(ctx.getSource().getPlayer()).getName().getString() : this.targetPlayer;

        CommonUtil.sendMessage(ctx, "&7获取数据中...");

        MutableComponent message = TextUtil.title(TextUtil.literal("&e" + targetPlayerName + "&f " + ((ConfigUtil.getPeriodTag().equals("default")) ? "" : ("在 &b" + ConfigUtil.getPeriodTag() + "&f ")) + "的统计数据&r"));

        return CommonUtil.tryReturn(() -> {
            PlaytimeRecord playtimeRecord = CommonUtil.waitFor(PlaytimeRecord.from(DBUtil.getManager(), ConfigUtil.getPeriodTag(), targetPlayerName));
            if (playtimeRecord != null) {
                message.append(
                        TextUtil.literal(
                                "&f累计在线时长：&e" + TextUtil.formatSeconds(playtimeRecord.getTotal()) + "\n" +
                                        "&f挂机时长：&c" + TextUtil.formatSeconds(playtimeRecord.getAfk()) + "\n" +
                                        "&f有效在线时长：&a" + TextUtil.formatSeconds(playtimeRecord.getValidTime()) + "\n"
                        )
                );
            }

            List<LoginRecord> loginRecords = CommonUtil.waitFor(LoginRecord.from(DBUtil.getManager(), targetPlayerName, ConfigUtil.getPeriodTag()));
            int loginSum = loginRecords.stream().filter(LoginRecord::isLogin).toList().size();
            if (loginSum > 0) {
                message.append(
                        TextUtil.literal("&f总登录次数：" + loginSum)
                );
            }

            ctx.getSource().sendSystemMessage(message);

            return 1;
        }, 0);
    }
}
