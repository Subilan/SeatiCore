package cc.seati.PlayerStats;

import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config {
    private static final String AFK_THRESHOLD_KICK = "playtime.afk-kick-threshold";
    private static final String AFK_THRESHOLD_NOTIFY = "playtime.afk-notify-threshold";
    private static final String AFK_MESSAGE_PATTERN = "playtime.afk-message-pattern";
    private static final String DATABASE_USERNAME = "database.username";
    private static final String DATABASE_PASSWORD = "database.password";
    private static final String DATABASE_CONNECTION_TIMEOUT = "database.connection-timeout";
    private static final String PERIOD_TAG = "period-tag";
    public static File configFile;
    public static YamlConfiguration t;

    public static void init() {
        configFile = new File("playerstats.yml");
        try {
            if (configFile.createNewFile()) {
                Main.LOGGER.info("Generated new configuration file at " + configFile.getAbsolutePath());
            } else {
                Main.LOGGER.info("Configuration file already exists. Skipping creation.");
            }
        } catch (IOException e) {
            Main.LOGGER.warn("Could not create new configuration file.");
            e.printStackTrace();
        }
        t = YamlConfiguration.loadConfiguration(configFile);

        Main.LOGGER.info("Initialized configuration.");
    }

    public static int getAfkKickThreshold() {
        return t.getInt(AFK_THRESHOLD_KICK, 3600);
    }

    public static int getAfkNotifyThreshold() {
        return t.getInt(AFK_THRESHOLD_NOTIFY, 300);
    }
    
    public static String getAfkMessagePattern() {
        return t.getString(AFK_MESSAGE_PATTERN, "$player is now afk.");
    }
    
    public static String getDatabaseUsername() {
        return t.getString(DATABASE_USERNAME);
    }
    
    public static String getDatabasePassword() {
        return t.getString(DATABASE_PASSWORD);
    }
    
    public static int getDatabaseConnectionTimeout() {
        return t.getInt(DATABASE_CONNECTION_TIMEOUT, 5);
    }

    public static String getPeriodTag() {
        return t.getString(PERIOD_TAG, "default");
    }
}
