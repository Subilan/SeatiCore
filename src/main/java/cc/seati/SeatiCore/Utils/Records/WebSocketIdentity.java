package cc.seati.SeatiCore.Utils.Records;

import cc.seati.SeatiCore.Utils.ConfigUtil;

public record WebSocketIdentity(String displayname, DecodedJWTPayload payload) {
    public boolean invalid() {
        return displayname.isEmpty() || (!isGuest() && payload == null);
    }

    public boolean isGuest() {
        return displayname.startsWith("Guest");
    }

    public boolean isAdmin() {
        return ConfigUtil.getWebsocketAdminList().contains(payload.username());
    }
}
