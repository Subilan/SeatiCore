package cc.seati.PlayerStats.Utils;

import cc.seati.PlayerStats.Main;
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection;

import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigUtil {
    private static final String AFK_THRESHOLD_KICK = "playtime.afk-kick-threshold";
    private static final String AFK_THRESHOLD_NOTIFY = "playtime.afk-notify-threshold";
    private static final String AFK_MESSAGE_PATTERN = "playtime.afk-message-pattern";
    private static final String DATABASE_USERNAME = "database.username";
    private static final String DATABASE_PASSWORD = "database.password";
    private static final String DATABASE_CONNECTION_TIMEOUT = "database.connection-timeout";
    private static final String PERIOD_TAG = "period-tag";
    private static final String PAGINATION_PAGE_SIZE = "pagination-page-size";
    private static final String ENABLE_FTB_RANKS_INTEGRATION = "enable-ftb-ranks-integration";
    private static final String RANK_REQUIREMENTS = "ranks";

    public static int getAfkKickThreshold() {
        return Main.config.t.getInt(AFK_THRESHOLD_KICK, 3600);
    }

    public static int getAfkNotifyThreshold() {
        return Main.config.t.getInt(AFK_THRESHOLD_NOTIFY, 300);
    }

    public static String getAfkMessagePattern() {
        return Main.config.t.getString(AFK_MESSAGE_PATTERN, "$player is now afk.");
    }

    public static String getDatabaseUsername() {
        return Main.config.t.getString(DATABASE_USERNAME);
    }

    public static String getDatabasePassword() {
        return Main.config.t.getString(DATABASE_PASSWORD);
    }

    public static int getDatabaseConnectionTimeout() {
        return Main.config.t.getInt(DATABASE_CONNECTION_TIMEOUT, 5);
    }

    public static String getPeriodTag() {
        return Main.config.t.getString(PERIOD_TAG, "default");
    }

    public static int getPaginationPageSize() {
        return Main.config.t.getInt(PAGINATION_PAGE_SIZE, 10);
    }

    public static void reload() {
        Main.config.reload();
    }

    public static boolean getEnableFTBRanksIntegration() {
        return Main.config.t.getBoolean(ENABLE_FTB_RANKS_INTEGRATION, false);
    }

    public static Map<String, Integer> getRankRequirements() {
        ConfigurationSection section = Main.config.t.getConfigurationSection(RANK_REQUIREMENTS);
        Map<String, Integer> map = new LinkedHashMap<>();
        if (section == null) return map;
        for (String key : section.getKeys(false)) {
            map.put(key, section.getInt(key));
        }
        return map;
    }
}
   