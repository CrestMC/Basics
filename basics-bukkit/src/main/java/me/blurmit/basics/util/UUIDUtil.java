package me.blurmit.basics.util;

import com.google.gson.JsonParser;
import me.blurmit.basics.Basics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class UUIDUtil {


    private static final Map<String, UUID> cachedPlayers = new HashMap<>();
    private static final Basics plugin = JavaPlugin.getPlugin(Basics.class);

    static {
        Bukkit.getScheduler().runTaskTimerAsynchronously(JavaPlugin.getPlugin(Basics.class), cachedPlayers::clear, 0L, 30 * 60 * 20L);
    }

    public static void asyncGetUUID(String username, Consumer<UUID> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> consumer.accept(getUUID(username)));
    }

    @Nullable
    public static UUID getUUID(String username) {
        if (!cachedPlayers.containsKey(username)) {
            try {
                URL apiServer = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
                InputStreamReader uuidReader = new InputStreamReader(apiServer.openStream());
                String uuidString = new JsonParser().parse(uuidReader).getAsJsonObject().get("id").getAsString();
                UUID uuid = UUID.fromString(uuidString.replaceFirst(
                        "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                        "$1-$2-$3-$4-$5"
                ));

                cachedPlayers.put(getName(uuid), uuid);
                return uuid;
            } catch (IOException | IllegalStateException e) {
                return null;
            }
        } else {
            return cachedPlayers.get(username);
        }
    }

    public static void asyncGetName(UUID uuid, Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> consumer.accept(getName(uuid)));
    }

    @Nullable
    public static String getName(UUID uuid) {
        if (!cachedPlayers.containsValue(uuid)) {
            try {
                String uuidString = uuid.toString().replace("-", "");
                URL sessionServer = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuidString);
                InputStreamReader nameReader = new InputStreamReader(sessionServer.openStream());

                String username = new JsonParser().parse(nameReader).getAsJsonObject().get("name").getAsString();
                cachedPlayers.put(username, uuid);

                return username;
            } catch (IOException | IllegalStateException e) {
                return null;
            }
        } else {
            return cachedPlayers.keySet().stream().filter(username -> cachedPlayers.get(username).equals(uuid)).findFirst().orElse(null);
        }
    }

}
