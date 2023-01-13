package me.blurmit.basicsbungee.limbo.server;

import net.md_5.bungee.BungeeServerInfo;
import net.md_5.bungee.api.ProxyServer;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class LimboServer extends BungeeServerInfo {

    public LimboServer() {
        super("Limbo", ProxyServer.getInstance().getConfig().getListeners().stream().findFirst().get().getSocketAddress(), "This place doesn't exist, anywhere!", false);
    }
}
