package cc.seati.PlayerStats.Utils;

import cc.seati.PlayerStats.Main;

public class RankUtil {
    public static boolean hasRank(String playername, String rankname) {
        return Main.ranks.t.getBoolean(playername + "." + rankname, false);
    }

    public static boolean setRank(String playername, String rankname, boolean bool) {
        Main.ranks.t.set(playername + "." + rankname, bool);
        Main.ranks.save();
        return CommonUtil.runCommand("ftbranks " + (bool ? "add" : "remove") + " " + playername + " " + rankname);
    }
}
