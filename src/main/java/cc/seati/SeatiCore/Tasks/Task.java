package cc.seati.SeatiCore.Tasks;

import cc.seati.SeatiCore.Utils.TextUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class Task {
    protected final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    protected final ScheduledExecutorService uptimeService = Executors.newSingleThreadScheduledExecutor();
    protected ScheduledFuture<?> taskFuture;
    protected ScheduledFuture<?> uptimeFuture;
    protected int uptime = 0;
    protected abstract void start();

    /**
     * 立即开始此任务并开始统计时间。
     */
    public void run() {
        this.start();
        uptimeFuture = uptimeService.scheduleAtFixedRate(() -> {
            this.uptime += 1;
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void shutdown() {
        executorService.shutdown();
        uptimeService.shutdown();
        this.uptime = 0;
    }

    public void cancel() {
        this.taskFuture.cancel(true);
        this.uptimeFuture.cancel(true);
        this.uptime = 0;
    }

    public boolean isRunning() {
        return !taskFuture.isDone();
    }

    public int getUptime() {
        return this.uptime;
    }

    public abstract int getInterval();

    public abstract @Nullable LocalDateTime getLastExecution();

    public abstract TaskType getType();

    public MutableComponent getExtraInfo() {
        return TextUtil.literal("&7此任务没有其它关键参数");
    }
}
