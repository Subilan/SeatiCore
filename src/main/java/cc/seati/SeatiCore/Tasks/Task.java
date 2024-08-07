package cc.seati.SeatiCore.Tasks;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public abstract class Task {
    protected final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    public abstract void run();
    public void shutdown() {
        executorService.shutdown();
    }
    public boolean isRunning() {
        return !executorService.isShutdown();
    }
}
