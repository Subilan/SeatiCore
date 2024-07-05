package cc.seati.PlayerStats;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class Text {

    /**
     * 利用替换后的字符串创建 MutableComponent
     * @param literal 目标字符串
     * @return MutableComponent
     */
    public static MutableComponent literal(String literal) {
        return Component.literal(translateAlternateColorCodes("&", literal));
    }

    /**
     * 将 str 中的所有 indicator 替换成 u00A7（§）用于表示颜色
     * @param indicator u00A7 的替代字符，通常为 &
     * @param str 字符串
     * @return 替换后的字符串
     */
    public static String translateAlternateColorCodes(String indicator, String str) {
        return str.replaceAll(indicator, "§");
    }

    /**
     * 将指定秒数格式化为 %dh%dm%ds 格式字符串
     * @param seconds 秒数
     * @return 格式化以后的字符串。例如 72800 格式化后为 20h13m20s
     */
    public static String formatSeconds(int seconds) {
        double h, m, s;
        h = Math.floor(seconds / 3600d);
        m = Math.floor(seconds % 3600 / 60d);
        s = seconds % 3600 % 60;

        return h + "h" + m + "m" + s + "s";
    }
}
