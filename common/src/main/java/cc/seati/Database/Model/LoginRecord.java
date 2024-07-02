package cc.seati.Database.Model;

import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.api.builder.TableQueryBuilder;
import cc.seati.Database.DataTables;
import cc.seati.Main;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LoginRecord {
    public static final String TABLE_NAME = DataTables.LOGIN_RECORDS.getTableName();
    private int id;
    private final LoginRecordActionType actionType;
    private int actionTypeValue;
    private Timestamp createdAt;
    private final String player;
    private boolean inserted = false;

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
     * 根据 action_type 获取 LoginRecords 列表
     * @param manager SQLManager
     * @param action_type 指定的 action_type
     * @return 指定 action_type 的 Completable Future
     */
    public static CompletableFuture<List<LoginRecord>> fromActionType(SQLManager manager, LoginRecordActionType action_type) {
        return getQueryBuilder(manager)
                .addCondition("action_type", action_type.value)
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
     * 根据 id 获取特定的 LoginRecord
     * @param manager SQLManager
     * @param id 指定 id
     * @return 指定的 LoginRecord（可能是 null）的 CompletableFuture
     */
    public static CompletableFuture<@Nullable LoginRecord> from(SQLManager manager, int id) {
        return getQueryBuilder(manager)
                .addCondition("id", id)
                .build()
                .executeFuture(q -> {
                    ResultSet rs = q.getResultSet();
                    if (rs.next()) {
                        return fromResultSet(rs);
                    }
                    return null;
                });
    }

    /**
     * 根据从数据库中获取的内容，创建一个 LoginRecord 实例
     *
     * @param id 数据库项
     * @param actionType 数据库项
     * @param createdAt 数据库项
     * @param player 数据库项
     */
    public LoginRecord(int id, boolean actionType, Timestamp createdAt, String player) {
        this.id = id;
        this.actionType = actionType ? LoginRecordActionType.LOGIN : LoginRecordActionType.LOGOUT;
        this.createdAt = createdAt;
        this.player = player;
        this.inserted = true;
    }

    /**
     * 手动创建一个新的记录
     *
     * @param actionType 枚举类型表示的登录或者登出
     * @param player 玩家名称
     */
    public LoginRecord(LoginRecordActionType actionType, String player) {
        this.actionType = actionType;
        this.actionTypeValue = actionType.value;
        this.player = player;
    }

    /**
     * 判断当前实例所代表的记录是否为数据表中存在的记录
     *
     * @return 是否是数据表中存在的记录
     */
    public boolean isRecord() {
        return this.inserted;
    }

    public void save(SQLManager manager) {
        manager.createInsert(TABLE_NAME)
                .setColumnNames("action_type", "player")
                .setParams(this.actionTypeValue, this.player)
                .executeAsync(q -> {
                    this.inserted = true;
                }, (e, a) -> {
                    Main.LOGGER.warning("Error saving player login record.");
                    Main.LOGGER.warning("SQL: " + a.getSQLContent());
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
