package cc.seati.SeatiCore.Utils;

import cc.seati.SeatiCore.Main;
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection;

import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigUtil {
    private static final String AFK_THRESHOLD_KICK = "playtime.afk-kick-threshold";
    private static final String AFK_THRESHOLD_NOTIFY = "playtime.afk-notify-threshold";
    private static final String AFK_ENTERING_MESSAGE_PATTERN = "playtime.afk-message-pattern.entering";
    private static final String AFK_LEAVING_MESSAGE_PATTERN = "playtime.afk-message-pattern.leaving";
    private static final String DATABASE_HOST = "database.host";
    private static final String DATABASE_NAME = "database.name";
    private static final String DATABASE_USERNAME = "database.username";
    private static final String DATABASE_PASSWORD = "database.password";
    private static final String DATABASE_CONNECTION_TIMEOUT = "database.connection-timeout";
    private static final String PERIOD_TAG = "period-tag";
    private static final String PAGINATION_PAGE_SIZE = "pagination-page-size";
    private static final String ENABLE_FTB_RANKS_INTEGRATION = "enable-ftb-ranks-integration";
    private static final String RANK_REQUIREMENTS = "ranks";
    private static final String ONLINE_PLAYERS_SNAPSHOT_INTERVAL = "online-players-snapshot-interval";
    private static final String ENABLE_WEBSOCKET_SERVER = "ws.enable";
    private static final String WEBSOCKET_SERVER_PORT = "ws.port";
    private static final String WEBSOCKET_JWT_SECRET = "ws.jwt-secret";
    private static final String API_SERVER_SECRET = "api.server-secret";
    private static final String API_HOST = "api.host";
    private static final String MAX_EMPTY_TIME = "max-empty-time";
    private static final String OSS_BACKUP_SCRIPT = "oss.backup-script";
    private static final String OSS_ARCHIVE_SCRIPT = "oss.archive-script";
    private static final String OSS_BACKUP_INTERVAL = "oss.backup-interval";
    private static final String OSS_UPLOAD_TIMEOUT = "oss.upload-timeout";

    public static int getAfkKickThreshold() {
        return Main.config.target().getInt(AFK_THRESHOLD_KICK, 3600);
    }

    public static int getAfkNotifyThreshold() {
        return Main.config.target().getInt(AFK_THRESHOLD_NOTIFY, 300);
    }

    public static String getAfkEnteringMessagePattern() {
        return Main.config.target().getString(AFK_ENTERING_MESSAGE_PATTERN, "$player is now afk.");
    }

    public static String getAfkLeavingMessagePattern() {
        return Main.config.target().getString(AFK_LEAVING_MESSAGE_PATTERN, "$player is no longer afk.");
    }

    public static String getDatabaseUsername() {
        return Main.config.target().getString(DATABASE_USERNAME);
    }

    public static String getDatabasePassword() {
        return Main.config.target().getString(DATABASE_PASSWORD);
    }

    public static String getDatabaseHost() {
        return Main.config.target().getString(DATABASE_HOST);
    }

    public static String getDatabaseName() {
        return Main.config.target().getString(DATABASE_NAME);
    }

    public static int getDatabaseConnectionTimeout() {
        return Main.config.target().getInt(DATABASE_CONNECTION_TIMEOUT, 5);
    }

    public static String getPeriodTag() {
        return Main.config.target().getString(PERIOD_TAG, "default");
    }

    public static int getPaginationPageSize() {
        return Main.config.target().getInt(PAGINATION_PAGE_SIZE, 10);
    }

    public static void reload() {
        Main.config.reload();
        Main.LOGGER.info("Reloaded {}.yml", Main.config.getFilename());
    }

    public static boolean getEnableFTBRanksIntegration() {
        return Main.config.target().getBoolean(ENABLE_FTB_RANKS_INTEGRATION, false);
    }

    public static Map<String, Integer> getRankRequirements() {
        ConfigurationSection section = Main.config.target().getConfigurationSection(RANK_REQUIREMENTS);
        Map<String, Integer> map = new LinkedHashMap<>();
        if (section == null) return map;
        for (String key : section.getKeys(false)) {
            map.put(key, section.getInt(key));
        }
        return map;
    }

    public static int getOnlinePlayersSnapshotInterval() {
        return Main.config.target().getInt(ONLINE_PLAYERS_SNAPSHOT_INTERVAL, 10);
    }

    public static boolean getEnableWebsocketServer() {
        return Main.config.target().getBoolean(ENABLE_WEBSOCKET_SERVER, false);
    }

    public static int getWebsocketServerPort() {
        return Main.config.target().getInt(WEBSOCKET_SERVER_PORT, 25500);
    }

    public static String getWebsocketJwtSecret() {
        return Main.config.target().getString(WEBSOCKET_JWT_SECRET);
    }

    public static String getApiServerSecret() {
        return Main.config.target().getString(API_SERVER_SECRET, "");
    }

    public static String getApiHost() {
        return Main.config.target().getString(API_HOST, "http://127.0.0.1");
    }

    public static Integer getMaxEmptyTime() {
        return Main.config.target().getInt(MAX_EMPTY_TIME, 3600);
    }

    public static String getOssBackupScript() {
        return Main.config.target().getString(OSS_BACKUP_SCRIPT);
    }

    public static String getOssArchiveScript() {
        return Main.config.target().getString(OSS_ARCHIVE_SCRIPT);
    }

    public static int getOssBackupInterval() {
        return Main.config.target().getInt(OSS_BACKUP_INTERVAL, 3600);
    }

    public static int getOssUploadTimeout() {
        return Main.config.target().getInt(OSS_UPLOAD_TIMEOUT, 120);
    }
}
   