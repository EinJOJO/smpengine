package it.einjojo.smpengine.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.UUID;

public class NameUUIDCache {
    private final static Cache<String, UUID> nameUUIDCache = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(5))
            .build();
    private static final String NAME_TO_UUID_URL = "https://api.mojang.com/users/profiles/minecraft/%s";

    public static UUID getUUID(String name) {
        return nameUUIDCache.get(name, NameUUIDCache::fetchUUID);
    }

    private static UUID fetchUUID(String name) {
        try {
            System.out.println("Fetching UUID for " + name);
            HttpURLConnection connection = (HttpURLConnection) new URL(String.format(NAME_TO_UUID_URL, name)).openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            if (connection.getResponseCode() == 200) {
                String response = new String(connection.getInputStream().readAllBytes());
                String uuidString = new StringBuilder(response.substring(12, 44))
                        .insert(8, "-")
                        .insert(13, "-")
                        .insert(18, "-")
                        .insert(23, "-")
                        .toString();
                return UUID.fromString(uuidString);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
