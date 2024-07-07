package cc.seati.PlayerStats.Commands;

import cc.seati.PlayerStats.Database.Model.LoginRecord;
import cc.seati.PlayerStats.Database.Model.PlaytimeRecord;
import cc.seati.PlayerStats.Utils.TextUtil;
import cc.seati.PlayerStats.Utils.CommonUtil;
import cc.seati.PlayerStats.Utils.ConfigUtil;
import cc.seati.PlayerStats.Utils.DBUtil;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.MutableComponent;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CommandBoard extends Command {
    private final String type;
    private final int page;
    private final int pageSize;

    public CommandBoard(String type, int page) {
        this.type = type;
        this.page = page;
        this.pageSize = ConfigUtil.getPaginationPageSize();
    }

    @Override
    public int handle(CommandContext<CommandSourceStack> ctx) {
        CommonUtil.sendMessage(ctx, "&7获取数据中...");

        if (this.page <= 0) {
            CommonUtil.sendMessage(ctx, "&c无效页码");
            return 1;
        }

        int fromIndex = pageSize * (page - 1);

        return switch (this.type) {
            case "total-time", "afk-time", "valid-time", "t", "a", "v" -> CommonUtil.tryReturn(() -> {
                List<PlaytimeRecord> record = CommonUtil.waitFor(PlaytimeRecord.from(DBUtil.getManager(), ConfigUtil.getPeriodTag())).stream().sorted(Comparator.comparingInt(switch (this.type) {
                    case "total-time", "t" -> PlaytimeRecord::getTotal;
                    case "afk-time", "a" -> PlaytimeRecord::getAfk;
                    case "valid-time", "v" -> PlaytimeRecord::getValidTime;
                    default -> throw new IllegalStateException("Unexpected value: " + this.type);
                }).reversed()).toList();

                if (record.isEmpty()) {
                    CommonUtil.sendMessage(ctx, "&7暂无相关数据");
                    return 1;
                }

                int maximumPage = (int) Math.ceil((double) record.size() / pageSize);
                int toIndex = Math.min(pageSize * page, record.size());

                if (fromIndex > record.size() - 1) {
                    CommonUtil.sendMessage(ctx, "&7此页不存在");
                    return 1;
                }

                // Note that List.subList accepts the 1st argument as inclusive bound and the 2nd as exclusive bound.
                List<PlaytimeRecord> sublist = record.subList(fromIndex, toIndex);

                MutableComponent message = TextUtil.title(TextUtil.literal((switch (this.type) {
                    case "total-time", "t" -> "&e总在线时长&f";
                    case "afk-time", "a" -> "&c挂机时长&f";
                    case "valid-time", "v" -> "&a有效时长&f";
                    default -> throw new IllegalStateException("Unexpected value: " + this.type);
                }) + "&f排行榜 [&a实时&f]"));

                AtomicInteger index = new AtomicInteger(1);
                sublist.forEach(value -> {
                    message.append(TextUtil.literal(
                            "[" + (this.type.equals("valid-time") || this.type.equals("v") ? CommonUtil.getColoredIndex(index.get()) : index.toString()) + "] " + value.getPlayer() + " &7-&f " + TextUtil.formatSeconds(switch (this.type) {
                                case "total-time", "t" -> value.getTotal();
                                case "afk-time", "a" -> value.getAfk();
                                case "valid-time", "v" -> value.getValidTime();
                                default -> throw new IllegalStateException("Unexpected value: " + this.type);
                            }) + "\n"
                    ));
                    index.addAndGet(1);
                });

                message.append(TextUtil.literal("\n&7第 &e" + page + "&7/" + maximumPage + " 页"));

                ctx.getSource().sendSystemMessage(message);
                return 1;
            }, 0);

            case "login" -> CommonUtil.tryReturn(() -> {
                Map<String, Integer> records = CommonUtil.waitFor(LoginRecord.getAll(DBUtil.getManager()))
                        // Stream 1: Filter only logging-in records, group the login records by playername, creating Map<String, List<LoginRecord>>
                        .stream()
                        .filter(LoginRecord::isLogin)
                        .collect(Collectors.groupingBy(LoginRecord::getPlayer))
                        .entrySet()
                        // Stream 2: Mapping the map value (list) to its size to reflect the login times of a player
                        .stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size()))
                        .entrySet()
                        // Stream 3: Sort the map by value, creating LinkedHashMap to keep order
                        .stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

                int maximumPage = (int) Math.ceil((double) records.size() / pageSize);
                int toIndex = Math.min(pageSize * page, records.size());

                if (fromIndex > records.size() - 1) {
                    CommonUtil.sendMessage(ctx, "&7此页不存在");
                    return 1;
                }

                MutableComponent message = TextUtil.title(TextUtil.literal("&e登录次数&f排行榜"));

                List<String> keyList =  records.keySet().stream().toList();

                // To prevent IndexOutOfBoundsException
                if (toIndex > keyList.size()) {
                    CommonUtil.sendMessage(ctx, "&7此页不存在");
                    return 1;
                }

                AtomicInteger index = new AtomicInteger(1);
                keyList.subList(fromIndex, toIndex).forEach(playername -> {
                    message.append(TextUtil.literal(
                            "[" + CommonUtil.getColoredIndex(index.get()) + "] " + playername + " &7-&f " + records.get(playername) + "\n"
                    ));
                    index.getAndIncrement();
                });

                message.append(TextUtil.literal("\n&7第 &e" + page + "&7/" + maximumPage + " 页"));

                ctx.getSource().sendSystemMessage(message);
                return 1;
            }, 0);

            default -> {
                CommonUtil.sendMessage(ctx, "&7没有对应的排行榜数据");
                yield 1;
            }
        };
    }
}
