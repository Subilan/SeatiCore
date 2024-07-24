package cc.seati.SeatiCore.Utils;

import cc.seati.SeatiCore.Main;

public class RankUtil {
    public static boolean hasRank(String playername, String rankname) {
        return Main.ranks.target().getBoolean(playername + "." + rankname, false);
    }

    public static boolean setRank(String playername, String rankname, boolean bool) {
        Main.ranks.target().set(playername + "." + rankname, bool);
        Main.ranks.save();
        return CommonUtil.runCommand("ftbranks " + (bool ? "add" : "remove") + " " + playername + " " + rankname);
    }
}
