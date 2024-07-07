package cc.seati.PlayerStats.Database;

import cc.carm.lib.easysql.EasySQL;
import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.hikari.HikariConfig;
import cc.seati.PlayerStats.Main;
import cc.seati.PlayerStats.Utils.CommonUtil;
import cc.seati.PlayerStats.Utils.ConfigUtil;

public class Database {
    public HikariConfig config = new HikariConfig();
    public SQLManager manager;
    public boolean isValid = false;

    public Database() {
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
