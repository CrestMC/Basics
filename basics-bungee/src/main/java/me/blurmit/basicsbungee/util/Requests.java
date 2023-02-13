package me.blurmit.basicsbungee.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.StringJoiner;

public class Requests {

    public static String getHttpResponse(String method, String urlString, Map<String, String> requestBody) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/109.0");
            connection.setDoOutput(true);

            StringJoiner requestBodyBuilder = new StringJoiner("&");
            for (Map.Entry<String, String> entry : requestBody.entrySet()) {
                requestBodyBuilder.add(
                        URLEncoder.encode(
                            entry.getKey(),
                            "UTF-8"
                        )
                        + "=" +
                        URLEncoder.encode(
                            entry.getValue(),
                            "UTF-8"
                        )
                );
            }

            byte[] output = requestBodyBuilder.toString().getBytes(StandardCharsets.UTF_8);
            int length = output.length;

            connection.setFixedLengthStreamingMode(length);
            connection.connect();

            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(output);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder content = new StringBuilder();
            String input;

            while ((input = reader.readLine()) != null) {
                content.append(input);
            }

            reader.close();
            connection.disconnect();

            return content.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
