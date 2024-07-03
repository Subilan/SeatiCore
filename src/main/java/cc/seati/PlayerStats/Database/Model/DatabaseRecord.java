package cc.seati.PlayerStats.Database.Model;

public abstract class DatabaseRecord {
    protected boolean assoc = false;

    /**
     * 判断当前实例所代表的记录是否为数据表中存在的记录
     *
     * @return 是否是数据表中存在的记录
     */
    public boolean isAssociate() {
        return this.assoc;
    }
}
