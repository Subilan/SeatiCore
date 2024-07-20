package cc.seati.SeatiCore.Utils;

import cc.carm.lib.easysql.api.SQLManager;
import cc.seati.SeatiCore.Main;

public class DBUtil {
    public static SQLManager getManager() {
        return Main.database.manager;
    }
}
