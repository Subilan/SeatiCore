package cc.seati.SeatiCore.WebSocket;

import cc.seati.SeatiCore.Main;
import cc.seati.SeatiCore.Utils.TextUtil;
import cc.seati.SeatiCore.Utils.WebUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class WebSocketServer extends org.java_websocket.server.WebSocketServer {
    private final MinecraftServer server;
    public static final List<String> onlineNames = new ArrayList<>();

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

        String displayname;

        if (!params.containsKey("token")) {
            conn.send("Missing token, logged in as guest.");
            Random rand = new Random();
            displayname = "Guest" + (rand.nextInt(99999) + 10000);
        } else {
            if (!params.containsKey("displayname")) {
                conn.send("Rejected: issing required argument `displayname`.");
                conn.close();
                return;
            }

            String token = params.get("token");
            displayname = params.get("displayname");

            if (!WebUtil.isJWTValid(token)) {
                conn.send("Rejected: Invalid token provided.");
                conn.close();
                return;
            }
        }

        onlineNames.add(displayname);
        this.broadcastMessage(conn, withPrefix("&e" + displayname + "&f 加入了服务器聊天"));
        conn.setAttachment(displayname);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        if (conn.getAttachment() != null) this.broadcastMessage(conn, withPrefix("&e" + conn.getAttachment() + "&f 退出了服务器聊天"));
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        if (((String) conn.getAttachment()).startsWith("Guest")) {
            conn.send("You can't send message in Guest mode.");
            return;
        }
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
        Main.LOGGER.info("Started WebSocket server at *:{}", this.getPort());
    }
}