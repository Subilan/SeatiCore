package cc.seati.PlayerStats.Database.Model;

import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.api.builder.TableQueryBuilder;
import cc.seati.PlayerStats.Database.DataTables;
import cc.seati.PlayerStats.Main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LoginRecord extends DatabaseRecord {
    public static final String TABLE_NAME = DataTables.LOGIN_RECORDS.getTableName();
    private int id;
    private final LoginRecordActionType actionType;
    private int actionTypeValue;
    private Timestamp createdAt;
    private final String player;

    public static LoginRecord fromResultSet(ResultSet rs) throws SQLException {
        return new LoginRecord(
                rs.getInt("id"),
                rs.getBoolean("action_type"),
                rs.getTimestamp("created_at"),
                rs.getString("player")
        );
    }

    public static TableQueryBuilder getQueryBuilder(SQLManager manager) {
        return manager.createQuery()
                .inTable(TABLE_NAME)
                .selectColumns("id", "action_type", "created_at", "player");
    }

    /**
     * 根据玩家获取 LoginRecords 列表
     *
     * @param manager    SQLManager
     * @param playername 玩家名称
     * @return 指定玩家登录记录的 Future，如果不存在任何登录记录，那么会返回一个空的列表
     */
    public static CompletableFuture<List<LoginRecord>> from(SQLManager manager, String playername) {
        return getQueryBuilder(manager)
                .addCondition("player", playername)
                .selectColumns("id", "action_type", "created_at", "player")
                .build()
                .executeFuture(q -> {
                    List<LoginRecord> records = new ArrayList<>();
                    ResultSet rs = q.getResultSet();
                    while (rs.next()) {
                        records.add(fromResultSet(rs));
                    }
                    return records;
                });
    }

    /**
     * 根据从数据库中获取的内容，创建一个 LoginRecord 实例
     *
     * @param id         数据库项
     * @param actionType 数据库项
     * @param createdAt  数据库项
     * @param player     数据库项
     */
    public LoginRecord(int id, boolean actionType, Timestamp createdAt, String player) {
        this.id = id;
        this.actionType = actionType ? LoginRecordActionType.LOGIN : LoginRecordActionType.LOGOUT;
        this.createdAt = createdAt;
        this.player = player;
        this.associate = true;
    }

    /**
     * 手动创建一个新的记录
     *
     * @param actionType 枚举类型表示的登录或者登出
     * @param player     玩家名称
     */
    public LoginRecord(LoginRecordActionType actionType, String player) {
        this.actionType = actionType;
        this.actionTypeValue = actionType.value;
        this.player = player;
    }

    /**
     * 将数据保存到数据库中
     *
     * @param manager SQLManager
     */
    public void saveAsync(SQLManager manager) {
        manager.createInsert(TABLE_NAME)
                .setColumnNames("action_type", "player")
                .setParams(this.actionTypeValue, this.player)
                .executeAsync(q -> {
                    this.associate = true;
                }, (e, a) -> {
                    Main.LOGGER.warn("Error saving player login record.");
                    e.printStackTrace();
                });
    }

    public boolean isLogin() {
        return this.actionType.isLogin();
    }

    public boolean isLogout() {
        return this.actionType.isLogout();
    }

    public String getPlayer() {
        return this.player;
    }

    public int getId() {
        return this.id;
    }

    public Timestamp getTime() {
        return this.createdAt;
    }
}
