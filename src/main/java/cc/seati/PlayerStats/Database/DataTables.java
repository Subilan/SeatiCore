package cc.seati.PlayerStats.Database;

import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.api.SQLTable;
import cc.carm.lib.easysql.api.builder.TableCreateBuilder;
import cc.seati.PlayerStats.Main;
import cc.seati.PlayerStats.Utils.CommonUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public enum DataTables implements SQLTable {
    LOGIN_RECORDS(table -> {
        table.addAutoIncrementColumn("id", true);
        table.addColumn("action_type", "BOOLEAN NOT NULL COMMENT '1 for login, 0 for logout'");
        table.addColumn("created_at", "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP");
        table.addColumn("tag", "VARCHAR(20) NOT NULL");
        table.addColumn("player", "VARCHAR(20) NOT NULL");
    }),

    PLAYTIME_RECORDS(table -> {
        table.addAutoIncrementColumn("id", true);
        table.addColumn("total", "INT NOT NULL DEFAULT 0 COMMENT 'in seconds'");
        table.addColumn("afk", "INT NOT NULL DEFAULT 0 COMMENT 'in seconds'");
        table.addColumn("updated_at", "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP");
        table.addColumn("tag", "VARCHAR(20) NOT NULL");
        table.addColumn("player", "VARCHAR(20) NOT NULL");
        table.addColumn("first", "BOOLEAN NOT NULL DEFAULT 0");
    }),
    
    SNAPSHOT_ONLINE_PLAYERS(table -> {
        table.addAutoIncrementColumn("id", true);
        table.addColumn("count", "INT NOT NULL");
        table.addColumn("names", "TEXT NOT NULL");
        table.addColumn("created_at", "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP");
    });

    private final Consumer<TableCreateBuilder> builder;
    private @Nullable SQLManager manager;

    DataTables(Consumer<TableCreateBuilder> builder) {
        this.builder = builder;
    }

    @Override
    public @Nullable SQLManager getSQLManager() {
        return this.manager;
    }

    @Override
    public @NotNull String getTableName() {
        return name().toLowerCase();
    }

    /**
     * 利用 sqlManager 创建当前枚举项实例的数据库表，并在表名前添加前缀 tablePrefix
     *
     * @param sqlManager SQLManager 实例
     * @return 是否创建成功
     */
    @Override
    public boolean create(@NotNull SQLManager sqlManager) {
        if (this.manager == null) this.manager = sqlManager;

        TableCreateBuilder tableBuilder = sqlManager.createTable(getTableName());
        if (builder != null) builder.accept(tableBuilder);
        return CommonUtil.tryReturn(() -> {
            tableBuilder.build().execute();
            return true;
        }, false);
    }

    public static void initialize(@NotNull SQLManager manager) {
        for (DataTables value : values()) {
            if (value.create(manager)) {
                Main.LOGGER.info("Initialized table " + value.getTableName());
            } else {
                Main.LOGGER.error("Could not create table " + value.getTableName());
            }
        }
    }
}
