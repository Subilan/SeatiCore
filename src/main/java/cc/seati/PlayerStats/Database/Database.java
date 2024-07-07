package cc.seati.PlayerStats.Database;

import cc.carm.lib.easysql.EasySQL;
import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.hikari.HikariConfig;
import cc.seati.PlayerStats.Main;
import cc.seati.PlayerStats.Utils.CommonUtil;
import cc.seati.PlayerStats.Utils.ConfigUtil;

public class Database {
    public static HikariConfig config = new HikariConfig();
    public static SQLManager manager;
    public static boolean isValid = false;

    public static void init() {
        config.setJdbcUrl("jdbc:mysql://localhost:3306/playerstats");
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setUsername(ConfigUtil.getDatabaseUsername());
        config.setPassword(ConfigUtil.getDatabasePassword());
        manager = EasySQL.createManager(config);

        CommonUtil.tryExec(() -> {
            if (
                    !manager.getConnection().isValid(
                            ConfigUtil.getDatabaseConnectionTimeout()
                    )
            ) {
                Main.LOGGER.info("Database connection timeout.");
            } else {
                Main.LOGGER.info("Successfully connected to database.");
                isValid = true;
            }
            return null;
        });

        Main.LOGGER.info("Initializing database...");
        DataTables.initialize(manager);
    }
}
