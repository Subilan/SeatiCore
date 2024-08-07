package cc.seati.SeatiCore.Tasks;

public enum TaskType {
    BACKUP_SERVER("backupServer"),
    EMPTY_SERVER("emptyServer"),
    PLAYER_SNAPSHOT("playerSnapshot"),
    PLAYTIME("__private__playtime");

    public final String name;

    TaskType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
