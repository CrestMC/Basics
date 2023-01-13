package me.blurmit.basicsbungee.util.pluginmessage;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

public class PluginMessageHelper {

    /**
    * Sends a plugin message to a specified server
    * @param name Server name that will be receiving the message (Use "all" for all servers, "receivers" for all servers except for the sender)
    * @param subchannel The subchannel in which the plugin message will be sent to
    * @param message The data that will be sent
     */
    public static void sendData(String name, String sender, String subchannel, String... message) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF(subchannel);

        if (message.length == 1) {
            output.writeUTF(message[0]);
        } else {
            for (String data : message) {
                output.writeUTF(data);
            }
        }

        byte[] data = output.toByteArray();
        if (data.length != 0) {
            switch (name.toLowerCase()) {
                case "all": {
                    ProxyServer.getInstance().getServers().forEach((server, info) -> info.sendData("BungeeCord", data, false));
                    return;
                }
                case "receivers": {
                    ProxyServer.getInstance().getServers().forEach((server, info) -> {
                        if (info.getName().equalsIgnoreCase(sender)) {
                            return;
                        }

                        if (info.getPlayers().size() == 0) {
                            return;
                        }

                        info.sendData("BungeeCord", data, false);
                    });
                    return;
                }
            }

            ServerInfo server = ProxyServer.getInstance().getServerInfo(name);
            server.sendData("BungeeCord", data);
        }
    }

}
