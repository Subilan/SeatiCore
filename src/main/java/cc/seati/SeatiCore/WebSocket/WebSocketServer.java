package cc.seati.SeatiCore.WebSocket;

import cc.seati.SeatiCore.Main;
import cc.seati.SeatiCore.Utils.CommonUtil;
import cc.seati.SeatiCore.Utils.Records.DecodedJWTPayload;
import cc.seati.SeatiCore.Utils.Records.WebSocketIdentity;
import cc.seati.SeatiCore.Utils.TextUtil;
import cc.seati.SeatiCore.Utils.WebUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.jetbrains.annotations.Nullable;

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

        if (params.containsKey("ping")) {
            conn.send("pong");
            conn.close();
            return;
        }

        String displayname;
        WebSocketIdentity identity;

        if (!params.containsKey("token")) {
            conn.send("Missing token, logged in as guest.");
            Random rand = new Random();
            displayname = "Guest" + (rand.nextInt(99999) + 10000);
            identity = new WebSocketIdentity(displayname, null);
        } else {
            if (!params.containsKey("displayname")) {
                conn.send("Rejected: missing required argument `displayname`.");
                conn.close();
                return;
            }

            if (!WebUtil.isJWTValid(params.get("token"))) {
                conn.send("Rejected: Invalid token provided.");
                conn.close();
                return;
            }

            displayname = params.get("displayname");
            identity = new WebSocketIdentity(displayname, WebUtil.decodeJWT(params.get("token")));
        }

        onlineNames.add(displayname);
        this.broadcastMessage(conn, withPrefix("&e" + displayname + "&f 加入了服务器聊天"));
        conn.setAttachment(identity);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        WebSocketIdentity id = a(conn);
        if (id.invalid()) return;

        this.broadcastMessage(conn, withPrefix("&e" + id.displayname() + "&f 退出了服务器聊天"));
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        WebSocketIdentity id = a(conn);
        if (id.invalid()) return;

        if (id.isGuest()) {
            conn.send("You can't send message in Guest mode.");
            return;
        }

        if (message.startsWith("/")) {
            if (id.isAdmin()) {
                CommonUtil.runCommand(message.substring(1));
            } else {
                conn.send("You don't have permission to use command in web GUI.");
            }
        } else {
            this.broadcastMessage(conn, senderSay(id.displayname(), message));
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    public static Component withPrefix(String content) {
        return TextUtil.literal("&a[Web] " + content);
    }

    public static Component senderSay(String displayname, String content) {
        return withPrefix("&f<" + displayname + "> " + content);
    }

    @Override
    public void onStart() {
        Main.LOGGER.info("Started WebSocket server at *:{}", this.getPort());
    }

    private WebSocketIdentity a(WebSocket conn) {
        if (conn.getAttachment() != null) return conn.getAttachment();
        return new WebSocketIdentity("", null);
    }
}