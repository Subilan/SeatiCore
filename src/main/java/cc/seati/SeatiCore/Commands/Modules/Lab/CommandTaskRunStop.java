package cc.seati.SeatiCore.Commands.Modules.Lab;

import cc.seati.SeatiCore.Commands.Abstract.Command;
import cc.seati.SeatiCore.Tasks.Task;
import cc.seati.SeatiCore.Tasks.TaskType;
import cc.seati.SeatiCore.Utils.CommonUtil;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;

public class CommandTaskRunStop extends Command {
    public String taskName;
    public boolean run;

    public CommandTaskRunStop(String taskName, boolean run) {
        this.taskName = taskName;
        this.run = run;
    }

    @Override
    public int handle(CommandContext<CommandSourceStack> ctx) {
        TaskType type = TaskType.of(this.taskName);
        if (type == null) {
            CommonUtil.sendMessage(ctx, "&c找不到名为 &e" + this.taskName + " &c的任务");
            return 1;
        }

        Task targetTask = type.toMainTask();
        if (this.run && targetTask.isRunning()) {
            CommonUtil.sendMessage(ctx, "&e指定任务已经在运行\n&e输入 &b/seati lab taskinfo " + this.taskName + "&e 查看详细信息");
            return 1;
        }

        if (!this.run && !targetTask.isRunning()) {
            CommonUtil.sendMessage(ctx, "&e指定任务已经停止\n&e输入 &b/seati lab taskinfo " + this.taskName + "&e 查看详细信息");
            return 1;
        }

        if (this.run) targetTask.run();
        else targetTask.shutdown();
        CommonUtil.sendMessage(ctx, (this.run ? "&a开始" : "&c停止") + "&e运行任务 &e" + this.taskName);
        return 1;
    }
}
