package cc.seati.SeatiCore.Utils;

import cc.seati.SeatiCore.Utils.Records.DecodedJWTPayload;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class WebUtil {

    public static @Nullable DecodedJWTPayload decodeJWT(String token) {
        @Nullable DecodedJWT decodedJWT;

        decodedJWT = CommonUtil.tryReturn(() -> {
            Algorithm algo = Algorithm.HMAC256(ConfigUtil.getWebsocketJwtSecret());
            JWTVerifier verifier = JWT.require(algo)
                    .withIssuer("seati")
                    .build();
            return verifier.verify(token);
        }, null);

        if (decodedJWT == null) return null;


        // The string being parsed here is of the format {"exp": ..., "iat": ..., "payload": {}}
        JsonObject payload = parseJson(fromUTF8(decodedJWT.getPayload())).get("payload").getAsJsonObject();
        // We do not need to check for `exp` here because a TokenExpiredException will be thrown above if the token is already expired.
        return new DecodedJWTPayload(payload.get("username").getAsString(), payload.get("updatedAt").getAsString());
    }

    public static boolean isJWTValid(String token) {
        return decodeJWT(token) != null;
    }

    public static JsonObject parseJson(String str) {
        return JsonParser.parseString(str).getAsJsonObject();
    }

    public static String fromUTF8(String str) {
        return new String(Base64.getDecoder().decode(str), StandardCharsets.UTF_8);
    }

    public static Map<String, String> splitQuery(String URL) {
        return CommonUtil.tryReturn(() -> {
            Map<String, String> query_pairs = new LinkedHashMap<>();
            @Nullable String query = new URI(URL).getQuery();
            if (query == null) {
                return new LinkedHashMap<>();
            }
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                query_pairs.put(URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8), URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8));
            }
            return query_pairs;
        }, new LinkedHashMap<>());
    }

    public static HttpClient getHttpClient() {
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    public static CompletableFuture<String> GET(String uri) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .timeout(Duration.ofSeconds(5))
                .setHeader("Seati-Server-Secret", ConfigUtil.getApiServerSecret())
                .GET()
                .build();

        return getHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
    }
}
