package cc.seati.PlayerStats.Database.Model;

import cc.carm.lib.easysql.api.SQLManager;
import cc.seati.PlayerStats.Database.DataTables;
import cc.seati.PlayerStats.Main;
import cc.seati.PlayerStats.Utils;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Future;

public class PlaytimeRecord extends DatabaseRecord {
    public static final String TABLE_NAME = DataTables.PLAYTIME_RECORDS.getTableName();
    private int id;
    private int total;
    private int afk;
    private Timestamp updatedAt;
    private final String tag;
    private final String player;

    public PlaytimeRecord(int id, int total, int afk, Timestamp updatedAt, String tag, String player) {
        this.id = id;
        this.total = total;
        this.afk = afk;
        this.updatedAt = updatedAt;
        this.tag = tag;
        this.player = player;
        this.associate = true;
    }

    public PlaytimeRecord(int total, int afk, String tag, String player) {
        this.total = total;
        this.afk = afk;
        this.tag = tag;
        this.player = player;
    }

    /**
     * 验证该记录是否存在
     * @param manager SQLManager
     * @param tag Period tag
     * @param player 玩家名称
     * @return 关于是否存在的布尔值 Future
     */
    public static Future<Boolean> isPresent(SQLManager manager, String tag, String player) {
        return manager.createQuery()
                .inTable(TABLE_NAME)
                .addCondition("player", player)
                .addCondition("tag", tag)
                .selectColumns("id")
                .build()
                .executeFuture(q -> q.getResultSet().next());
    }

    public static Future<@Nullable PlaytimeRecord> from(SQLManager manager, String tag, String player) {
        return from(manager, tag, player, false);
    }

    /**
     * 从数据库中获取数据
     * @param manager SQLManager
     * @param tag Period tag
     * @param player 玩家名称
     * @param autoCreate 是否自动创建，如果设置为 true，当记录不存在时会自动创建；如果设置为 false，当记录不存在时返回 null。为了更好的 IDE 提示，设置为 false 时建议使用 <b>PlaytimeRecord.from(SQLManager, String, String)</b> 方法。
     * @return 对应的 PlaytimeRecord 实例
     */
    public static Future<PlaytimeRecord> from(SQLManager manager, String tag, String player, boolean autoCreate) {
        // Firstly check if the target record is present.
        return manager.createQuery()
                .inTable(TABLE_NAME)
                .addCondition("player", player)
                .addCondition("tag", tag)
                .selectColumns("id", "total", "afk", "tag", "player", "updated_at")
                .build()
                .executeFuture(q -> {
                    ResultSet rs = q.getResultSet();
                    // If present, return the record.
                    if (rs.next()) {
                        return new PlaytimeRecord(
                                rs.getInt("id"),
                                rs.getInt("total"),
                                rs.getInt("afk"),
                                rs.getTimestamp("updated_at"),
                                rs.getString("tag"),
                                rs.getString("player")
                        );
                    } else {
                        if (autoCreate) {
                            // or, create an empty record and return.
                            manager.createInsert(TABLE_NAME)
                                    .setColumnNames("total", "afk", "tag", "player")
                                    .setParams(0, 0, tag, player)
                                    .executeAsync();
                            PlaytimeRecord record = new PlaytimeRecord(0, 0, tag, player);
                            record.associate = true;
                            return record;
                        } else {
                            return null;
                        }
                    }
                });
    }

    /**
     * （阻塞方法）将数据保存到数据库中
     *
     * @param manager SQLManager
     */
    public void saveSync(SQLManager manager) {
        try {
            manager.createUpdate(TABLE_NAME)
                    .addCondition("player", this.player)
                    .addCondition("tag", this.tag)
                    .setColumnValues(new LinkedHashMap<>(
                            Map.of(
                                    "total", this.total,
                                    "afk", this.afk
                            )
                    ))
                    .build()
                    .execute();
        } catch (SQLException e) {
            Main.LOGGER.warn("Database operation failed.");
            e.printStackTrace();
        }
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public String getTag() {
        return tag;
    }

    public String getPlayer() {
        return player;
    }

    public int getAfk() {
        return afk;
    }

    public int getId() {
        return id;
    }

    public int getTotal() {
        return total;
    }

    public int getValidTime() {
        return total - afk;
    }

    public void setAfk(int afk) {
        this.afk = afk;
    }

    public void setTableName(int total) {
        this.total = total;
    }

    public void increaseAfk() {
        this.afk += 1;
        this.updatedAt = Utils.getCurrentTimestamp();
    }

    public void increaseTotal() {
        this.total += 1;
        this.updatedAt = Utils.getCurrentTimestamp();
    }
}
