package cc.seati.Database;

import cc.carm.lib.easysql.EasySQL;
import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.hikari.HikariConfig;
import cc.seati.Config;
import cc.seati.Main;

import java.sql.SQLException;

public class Database {
    public static HikariConfig config = new HikariConfig();
    public static SQLManager manager;
    public static boolean isValid = false;

    public static void init() {
        config.setJdbcUrl("jdbc:mysql://localhost:3306/playerdatatracer");
        config.setUsername(Config.t.getString("database.username", "root"));
        config.setPassword(Config.t.getString("database.password"));
        manager = EasySQL.createManager(config);

        try {
            if (
                    !manager.getConnection().isValid(
                            Config.t.getInt("database.connection-timeout", 5)
                    )
            ) {
                Main.LOGGER.info("Database connection timeout.");
            } else {
                Main.LOGGER.info("Successfully connected to database.");
                isValid = true;
            }
        } catch (SQLException e) {
            Main.LOGGER.warning("Could not connect to database.");
            e.printStackTrace();
        }

        Main.LOGGER.info("Initializing database...");
        DataTables.initialize(manager);
    }
}
