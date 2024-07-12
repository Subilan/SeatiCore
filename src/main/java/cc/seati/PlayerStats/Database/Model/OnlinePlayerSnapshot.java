package cc.seati.PlayerStats.Database.Model;

import cc.carm.lib.easysql.api.SQLManager;
import cc.seati.PlayerStats.Database.DataTables;
import cc.seati.PlayerStats.Main;

import java.sql.Timestamp;

public class OnlinePlayerSnapshot extends DatabaseRecord {
    public static final String TABLE_NAME = DataTables.SNAPSHOT_ONLINE_PLAYERS.getTableName();
    private int id;
    private final int count;
    private final String names;
    private Timestamp createdAt;

    public OnlinePlayerSnapshot(int id, int count, String names, Timestamp createdAt) {
        this.id = id;
        this.count = count;
        this.names = names;
        this.createdAt = createdAt;
        this.associate = true;
    }

    public OnlinePlayerSnapshot(int count, String names) {
        this.count = count;
        this.names = names;
    }

//    public static @Nullable OnlinePlayerSnapshot getLatest(SQLManager manager) {
//        return CommonUtil.tryReturn(() -> CommonUtil.waitFor(
//                manager.createQuery()
//                        .inTable(TABLE_NAME)
//                        .setLimit(1)
//                        .orderBy("id", false)
//                        .build()
//                        .executeFuture(r -> {
//                                    ResultSet rs = r.getResultSet();
//                                    if (rs.next()) {
//                                        return new OnlinePlayerSnapshot(
//                                                rs.getInt("id"),
//                                                rs.getInt("count"),
//                                                rs.getString("names"),
//                                                rs.getTimestamp("created_at")
//                                        );
//                                    } else {
//                                        return null;
//                                    }
//                                }
//                        )
//        ), null);
//    }

    public void saveAsync(SQLManager manager) {
        manager.createInsert(TABLE_NAME)
                .setColumnNames("count", "names")
                .setParams(this.count, this.names)
                .executeAsync(q -> {
                    this.associate = true;
                }, (e, a) -> {
                    Main.LOGGER.warn("Error saving player online player snapshot.");
                    e.printStackTrace();
                });
    }

    public int getId() {
        return id;
    }

    public int getCount() {
        return count;
    }

    public String getNames() {
        return names;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }
}
