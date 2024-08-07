package cc.seati.SeatiCore.Commands.Modules.Lab;

import cc.seati.SeatiCore.Commands.Abstract.Command;
import cc.seati.SeatiCore.Main;
import cc.seati.SeatiCore.Tasks.Task;
import cc.seati.SeatiCore.Utils.CommonUtil;
import cc.seati.SeatiCore.Utils.TextUtil;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

public class CommandTaskInfo extends Command {
    public String taskName;

    public CommandTaskInfo(String taskName) {
        this.taskName = taskName;
    }

    @Override
    public int handle(CommandContext<CommandSourceStack> ctx) {
        MutableComponent component = buildTaskInfo(this.taskName);
        if (component == null) {
            CommonUtil.sendMessage(ctx, "&c找不到 &e" + this.taskName + "&c 的相关信息");
        } else {
            CommonUtil.sendMessage(ctx, component);
        }
        return 1;
    }

    public @Nullable MutableComponent buildTaskInfo(String taskName) {
        Task targetTask = switch (taskName) {
            case "backupServer" -> Main.backupServerTask;
            case "emptyServer" -> Main.emptyServerTask;
            case "playerSnapshot" -> Main.playersSnapshotTask;
            default -> null;
        };

        if (targetTask == null) return null;

        boolean isRunning = targetTask.isRunning();
        int interval = targetTask.getInterval();
        LocalDateTime lastExecution = targetTask.getLastExecution();
        String lastExecutionString = lastExecution == null ? "暂未执行过" : TextUtil.formatLocalDateTime(lastExecution, TextUtil.DATE_PATTERN_NORMAL);
        return TextUtil.literal(
                "&a" + taskName + " &e任务的相关信息\n\n" +
                        (isRunning ? "&a✔ 运行中" : "&c✘ 已停止") + "\n" +
                        "&f执行频率：&e" + interval + "s\n" +
                        "&f上次执行时间：&e" + lastExecutionString + "\n" +
                        "&f连续运行时间：&e" + targetTask.getUptime() + "s\n"
        );
    }
}
