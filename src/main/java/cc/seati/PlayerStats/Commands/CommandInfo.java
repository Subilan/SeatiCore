package cc.seati.PlayerStats.Commands;

import cc.seati.PlayerStats.Config;
import cc.seati.PlayerStats.Database.Database;
import cc.seati.PlayerStats.Database.Model.LoginRecord;
import cc.seati.PlayerStats.Database.Model.PlaytimeRecord;
import cc.seati.PlayerStats.Text;
import cc.seati.PlayerStats.Utils;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class CommandInfo extends Command {

    public final String targetPlayer;

    public CommandInfo() {
        this.targetPlayer = "";
    }

    public CommandInfo(String targetPlayer) {
        this.targetPlayer = targetPlayer;
    }

    @Override
    public int handle(CommandContext<CommandSourceStack> ctx) {
        if (!ctx.getSource().isPlayer() && this.targetPlayer.isEmpty()) {
            Utils.sendMessageCtx(ctx, "This command can only be executed by player.");
            return 1;
        }

        String targetPlayerName = this.targetPlayer.isEmpty() ? Objects.requireNonNull(ctx.getSource().getPlayer()).getName().getString() : this.targetPlayer;

        Utils.sendMessageCtx(ctx, "获取数据中...");

        MutableComponent message = Text.literal("&e" + targetPlayerName + "&f 的统计数据（tag: " + Config.getPeriodTag() + "）\n\n");

        try {
            PlaytimeRecord playtimeRecord = Utils.waitFor(PlaytimeRecord.from(Database.manager, Config.getPeriodTag(), targetPlayerName));
            if (playtimeRecord != null) {
                message.append(
                        Text.literal(
                                "&f累计在线时长：&e" + Text.formatSeconds(playtimeRecord.getTotal()) + "\n" +
                                        "&f挂机时长：&c" + Text.formatSeconds(playtimeRecord.getAfk()) + "\n" +
                                        "&f有效在线时长：&a" + Text.formatSeconds(playtimeRecord.getValidTime()) + "\n"
                        )
                );
            }

            List<LoginRecord> loginRecords = Utils.waitFor(LoginRecord.from(Database.manager, targetPlayerName));
            int loginSum = loginRecords.stream().filter(LoginRecord::isLogin).toList().size();
            if (loginSum > 0) {
                message.append(
                        Text.literal("&f总登录次数：" + loginSum + "\n")
                );
            }
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
            return 0;
        }

        return 1;
    }
}
