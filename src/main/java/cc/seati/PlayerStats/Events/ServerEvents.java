package cc.seati.PlayerStats.Events;

import cc.seati.PlayerStats.Main;
import cc.seati.PlayerStats.Tracker.PlayersOnlineTracker;
import cc.seati.PlayerStats.Utils.CommonUtil;
import cc.seati.PlayerStats.Utils.ConfigUtil;
import cc.seati.PlayerStats.WebSocket.WebSocketServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.net.InetSocketAddress;

@Mod.EventBusSubscriber(Dist.DEDICATED_SERVER)
public class ServerEvents {
    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent e) {
        Main.server = e.getServer();
        Main.playersOnlineTracker = new PlayersOnlineTracker(ConfigUtil.getOnlinePlayersSnapshotInterval(), Main.server);
        Main.playersOnlineTracker.run();
        if (ConfigUtil.getEnableWebsocketServer()) {
            Main.wsServer = new WebSocketServer(new InetSocketAddress(ConfigUtil.getWebsocketServerPort()), Main.server);
            Main.wsThread = new Thread(() -> Main.wsServer.run());
            Main.wsThread.start();
        }
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent e) {
        CommonUtil.tryRun(() -> {
            Main.wsServer.stop();
            Main.playersOnlineTracker.shutdown();
            return null;
        }, ex -> {
            Main.LOGGER.error("Some external threads are unable to stop. Please try using Ctrl+C to resolve.");
            return null;
        });
    }
}
