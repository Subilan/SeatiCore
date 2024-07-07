package cc.seati.PlayerStats.Utils;

import cc.carm.lib.easysql.api.SQLManager;
import cc.seati.PlayerStats.Main;

public class DBUtil {
    public static SQLManager getManager() {
        return Main.database.manager;
    }
}
