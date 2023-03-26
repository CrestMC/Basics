package me.blurmit.basics.util;

import com.google.gson.Gson;
import me.blurmit.basics.Basics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpRequestUtil {

    public static void dispatchDiscordWebhook(String url, Map<String, Object> data) {
        if (!data.containsKey("content")) {
            data.put("content", "No content provided.");
        }

        Gson gson = new Gson();
        String jsonData = gson.toJson(data);

        sendData(url, jsonData);
    }

    public static void sendData(String url, String data) {
        sendHttpRequest(url, data);
    }

    public static void sendHttpRequest(String url, String data) {
        handleHttpPayload("POST", url, data);
    }

    public static void handleHttpPayload(String requestMethod, String urlString, String data) {
        Bukkit.getScheduler().runTaskAsynchronously(JavaPlugin.getPlugin(Basics.class), () -> {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod(requestMethod);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("User-Agent", "Java HTTP Request");
                connection.setDoOutput(true);

                byte[] output = data.getBytes(StandardCharsets.UTF_8);
                int length = output.length;

                connection.setFixedLengthStreamingMode(length);

                try (OutputStream outputStream = connection.getOutputStream()) {
                    outputStream.write(output);
                    outputStream.flush();
                }

                connection.getInputStream().close();
                connection.disconnect();
            } catch (IOException e) {
                Bukkit.getLogger().warning("Failed to send HTTP request to " + urlString + " due to a " + e.getClass().getName());
            }
        });
    }

}
