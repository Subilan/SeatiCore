package cc.seati.SeatiCore.Database.Model;

import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.api.builder.TableQueryBuilder;
import cc.seati.SeatiCore.Database.DataTables;
import cc.seati.SeatiCore.Database.Model.Enums.LoginRecordActionType;
import cc.seati.SeatiCore.Main;
import cc.seati.SeatiCore.Utils.CommonUtil;

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
    private final String tag;
    private final boolean first;

    public static LoginRecord fromResultSet(ResultSet rs) throws SQLException {
        return new LoginRecord(
                rs.getInt("id"),
                rs.getBoolean("action_type"),
                rs.getBoolean("first"),
                rs.getTimestamp("created_at"),
                rs.getString("player"),
                rs.getString("tag")
        );
    }

    public static TableQueryBuilder getQueryBuilder(SQLManager manager) {
        return manager.createQuery()
                .inTable(TABLE_NAME)
                .selectColumns("id", "action_type", "created_at", "player", "tag", "first");
    }

    /**
     * 根据玩家获取 LoginRecords 列表
     *
     * @param manager    SQLManager
     * @param playername 玩家名称
     * @param tag        Period tag
     * @return 指定玩家登录记录的 Future，如果不存在任何登录记录，那么会返回一个空的列表
     */
    public static CompletableFuture<List<LoginRecord>> from(SQLManager manager, String playername, String tag) {
        return getQueryBuilder(manager)
                .addCondition("player", playername)
                .addCondition("tag", tag)
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
     * 获取所有的 Login Record 数据
     *
     * @param manager SQLManager
     * @return 带有所有玩家 Login 记录 List 的 Future，如果不存在任何登录记录，那么会返回一个空的列表
     */
    public static CompletableFuture<List<LoginRecord>> getAll(SQLManager manager) {
        return getQueryBuilder(manager)
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
     * @param tag        数据库项
     */
    public LoginRecord(int id, boolean actionType, boolean first, Timestamp createdAt, String player, String tag) {
        this.id = id;
        this.actionType = actionType ? LoginRecordActionType.LOGIN : LoginRecordActionType.LOGOUT;
        this.createdAt = createdAt;
        this.first = first;
        this.player = player;
        this.associate = true;
        this.tag = tag;
    }

    /**
     * 手动创建一个新的记录
     *
     * @param actionType 枚举类型表示的登录或者登出
     * @param player     玩家名称
     */
    public LoginRecord(LoginRecordActionType actionType, String player, String tag, boolean first) {
        this.actionType = actionType;
        this.actionTypeValue = actionType.value;
        this.player = player;
        this.tag = tag;
        this.first = first;
    }

    /**
     * 将数据保存到数据库中
     *
     * @param manager SQLManager
     */
    public void saveAsync(SQLManager manager) {
        manager.createInsert(TABLE_NAME)
                .setColumnNames("action_type", "player", "tag", "first")
                .setParams(this.actionTypeValue, this.player, this.tag, this.first)
                .executeAsync(q -> {
                    this.associate = true;
                }, (e, a) -> {
                    Main.LOGGER.warn("Error saving player login record.");
                    e.printStackTrace();
                });
    }

    /**
     * 判断玩家在 tag 下是否登录过。此项通过判断玩家在 tag 下的登录记录是否为空来达成
     *
     * @param manager SQLManager
     * @param player  用户名
     * @param tag     Period tag
     * @return 如果获取数据过程出现问题，或者已经存在玩家任何一个登录记录，返回 false；否则返回 true
     */
    public static boolean isFirstLogin(SQLManager manager, String player, String tag) {
        return CommonUtil.tryReturn(() -> CommonUtil.waitFor(LoginRecord.from(manager, player, tag)).stream().noneMatch(LoginRecord::isLogin), false);
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

    public String getTag() {
        return this.tag;
    }

    public boolean isFirst() {
        return this.first;
    }
}
