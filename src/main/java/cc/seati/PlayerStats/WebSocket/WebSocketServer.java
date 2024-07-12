package cc.seati.PlayerStats.WebSocket;

import cc.seati.PlayerStats.Main;
import net.minecraft.server.MinecraftServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;

public class WebSocketServer extends org.java_websocket.server.WebSocketServer {
    private final MinecraftServer server;

    public WebSocketServer(InetSocketAddress address, MinecraftServer server) {
        super(address);
        this.server = server;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        conn.send("connected");
        System.out.println(handshake.getResourceDescriptor());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("connection closed");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("received " + message );
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.out.println("error");
    }

    @Override
    public void onStart() {
        Main.LOGGER.info("Started WebSocket server at " + this.getAddress().toString());
    }
}