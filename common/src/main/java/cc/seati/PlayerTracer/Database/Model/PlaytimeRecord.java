package cc.seati.PlayerTracer.Database.Model;

import cc.carm.lib.easysql.api.SQLManager;
import cc.seati.PlayerTracer.Database.DataTables;
import cc.seati.PlayerTracer.Main;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
        this.assoc = true;
    }

    public PlaytimeRecord(int total, int afk, String tag, String player) {
        this.total = total;
        this.afk = afk;
        this.tag = tag;
        this.player = player;
    }

    public static Future<@Nullable PlaytimeRecord> from(SQLManager manager, String tag, String player) {
        return manager.createQuery()
                .inTable(TABLE_NAME)
                .addCondition("player", player)
                .addCondition("tag", tag)
                .selectColumns("id", "total", "afk", "tag", "player", "updated_at")
                .build()
                .executeFuture(q -> {
                    ResultSet rs = q.getResultSet();
                    if (rs.next()) {
                        return new PlaytimeRecord(
                                rs.getInt("id"),
                                rs.getInt("total"),
                                rs.getInt("afk"),
                                rs.getTimestamp("updated_at"),
                                rs.getString("tag"),
                                rs.getString("player")
                        );
                    }
                    return null;
                });
    }

    public static Future<Boolean> exist(String tag, String player, SQLManager manager) {
        return manager.createQuery()
                .inTable(TABLE_NAME)
                .addCondition("player", player)
                .addCondition("tag", tag)
                .selectColumns("id")
                .build()
                .executeFuture(q -> {
                    ResultSet rs = q.getResultSet();
                    return rs.next();
                });
    }

    /**
     * （阻塞方法）将数据保存到数据库中
     * @param manager SQLManager
     */
    public void saveSync(SQLManager manager) {
        try {

            // Firstly check if there is already a record of the player with the period tag.
            // If not, insert a new record with the latest values.
            if (!exist(this.tag, this.player, manager).get(5, TimeUnit.SECONDS)) {
                manager.createInsert(TABLE_NAME)
                        .setColumnNames("total", "afk", "tag", "player")
                        .setParams(this.total, this.afk, this.tag, this.player)
                        .execute();
                this.assoc = true;
                return;
            }

            // If so, update the target row with condition of player and tag with the latest value.
            manager.createUpdate(TABLE_NAME)
                    .addCondition("player", this.player)
                    .addCondition("tag", this.tag)
                    .setColumnValues(new LinkedHashMap<>(Map.of(
                            "total", this.total,
                            "afk", this.afk
                    )))
                    .build()
                    .execute();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return;
        } catch (TimeoutException e) {
            Main.LOGGER.warning("Database operation timeout when saving playtime record.");
            e.printStackTrace();
            return;
        } catch (SQLException e) {
            Main.LOGGER.warning("Database operation failed.");
            e.printStackTrace();
            return;
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
        this.afk++;
    }

    public void increaseTotal() {
        this.total++;
    }
}
