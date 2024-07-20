package cc.seati.PlayerStats.Commands.Modules.Stats;

import cc.seati.PlayerStats.Commands.Abstract.Command;
import cc.seati.PlayerStats.Database.DataTables;
import cc.seati.PlayerStats.Utils.CommonUtil;
import cc.seati.PlayerStats.Utils.DBUtil;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;

import java.util.Arrays;

public class CommandMigrate extends Command {
    private final String from;
    private final String to;
    private final String table;

    public CommandMigrate(String table, String from, String to) {
        this.from = from;
        this.to = to;
        this.table = table;
    }

    @Override
    public int handle(CommandContext<CommandSourceStack> ctx) {

        if (!Arrays.stream(DataTables.values()).map(DataTables::getTableName).toList().contains(this.table)) {
            CommonUtil.sendMessage(ctx, "&c数据表 " + this.table + " 不存在");
            return 1;
        }

        DBUtil.getManager().createUpdate(this.table)
                .addCondition("player", this.from)
                .setColumnValues("player", this.to)
                .build()
                .executeAsync(
                        r -> CommonUtil.sendMessage(ctx, "&f[&a成功&f] &e迁移&f " + this.table + "&7: &a" + this.from + "&7 -> &a" + this.to),
                        (e, a) -> CommonUtil.sendMessage(ctx, "&f[&c失败&f] &e迁移&f " + this.table + "&7: &a" + this.from + "&7 -> &a" + this.to + "\n" + "&7" + e.getMessage())
                );

        return 1;
    }
}
