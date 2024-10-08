package cc.seati.SeatiCore.Utils;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TextUtil {
    public static final String DATE_PATTERN_NORMAL = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_PATTERN_UNDERSCORE = "yyyy-MM-dd_HH:mm:ss";

    /**
     * 利用替换后的字符串创建 MutableComponent
     * @param literal 目标字符串
     * @return MutableComponent
     */
    public static MutableComponent literal(String literal) {
        return Component.literal(translateAlternateColorCodes("&", literal));
    }

    public static MutableComponent title(Component component) {
        return literal("\n&e* &r").append(component).append(literal(" &e*\n&7---------------\n\n"));
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
        int h, m, s;
        h = (int) Math.floor(seconds / 3600d);
        m = (int) Math.floor(seconds % 3600 / 60d);
        s = seconds % 3600 % 60;

        return h + "h" + m + "m" + s + "s";
    }

    /**
     * 将指定 LocalDateTime 格式化为 yyyy-MM-dd HH:mm:ss 格式的字符串（本地时间）
     * @param ldt 要格式化的 LocalDateTime
     * @return 转换后的字符串
     */
    public static String formatLocalDateTime(LocalDateTime ldt, String pattern) {
        return ldt.format(DateTimeFormatter.ofPattern(pattern));
    }
}
