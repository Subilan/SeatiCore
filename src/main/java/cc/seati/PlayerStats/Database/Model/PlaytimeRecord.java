package cc.seati.PlayerStats.Database.Model;

import cc.carm.lib.easysql.api.SQLManager;
import cc.seati.PlayerStats.Database.DataTables;
import cc.seati.PlayerStats.Main;
import cc.seati.PlayerStats.Utils;

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

    public static Future<PlaytimeRecord> from(SQLManager manager, String tag, String player) {
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
                        // or, create an empty record and return.
                        manager.createInsert(TABLE_NAME)
                                .setColumnNames("total", "afk", "tag", "player")
                                .setParams(0, 0, tag, player)
                                .executeAsync();
                        PlaytimeRecord record = new PlaytimeRecord(0, 0, tag, player);
                        record.associate = true;
                        return record;
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
