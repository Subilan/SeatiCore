package cc.seati.PlayerStats.WebSocket;

import cc.seati.PlayerStats.Main;
import cc.seati.PlayerStats.Utils.Records.DecodedJWTPayload;
import cc.seati.PlayerStats.Utils.TextUtil;
import cc.seati.PlayerStats.Utils.WebUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WebSocketServer extends org.java_websocket.server.WebSocketServer {
    private final MinecraftServer server;
    private final List<String> onlineNames = new ArrayList<>();

    public WebSocketServer(InetSocketAddress address, MinecraftServer server) {
        super(address);
        this.server = server;
    }

    private void broadcastMessage(WebSocket conn, Component component) {
        this.broadcast(component.getString());
        this.server.getPlayerList().getPlayers().forEach(p -> p.sendSystemMessage(component));
        Main.LOGGER.info(component.getString());
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        conn.send("connected");
        Map<String, String> params = WebUtil.splitQuery(handshake.getResourceDescriptor());

        if (!params.containsKey("token")) {
            conn.send("Rejected: Missing required field 'token'.");
            conn.close();
            return;
        }

        String token = params.get("token");
        @Nullable DecodedJWTPayload payload = WebUtil.decodeJWT(token);

        if (payload == null) {
            conn.send("Rejected: Invalid token provided.");
            conn.close();
            return;
        }

        this.onlineNames.add(payload.username());
        this.broadcastMessage(conn, withPrefix("&e" + payload.username() + "&f 加入了服务器聊天"));
        conn.setAttachment(payload.username());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        if (conn.getAttachment() != null) this.broadcastMessage(conn, withPrefix("&e" + conn.getAttachment() + "&f 退出了服务器聊天"));
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        if (conn.getAttachment() != null) this.broadcastMessage(conn, senderSay(conn, message));
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    public static Component withPrefix(String content) {
        return TextUtil.literal("&a[Web] " + content);
    }

    public static Component senderSay(WebSocket conn, String content) {
        return withPrefix("&f<" + conn.getAttachment() + "> " + content);
    }

    @Override
    public void onStart() {
        Main.LOGGER.info("Started WebSocket server at *:" + this.getPort());
    }
}