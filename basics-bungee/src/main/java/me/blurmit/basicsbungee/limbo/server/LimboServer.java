package me.blurmit.basicsbungee.limbo.server;

import net.md_5.bungee.BungeeServerInfo;
import net.md_5.bungee.api.ProxyServer;

import java.net.InetSocketAddress;

public class LimboServer extends BungeeServerInfo {

    public LimboServer() {
        super("Limbo", InetSocketAddress.createUnresolved("0.0.0.0", ((InetSocketAddress) ProxyServer.getInstance().getConfig().getListeners().stream().findFirst().get().getSocketAddress()).getPort()), "This place doesn't exist, anywhere!", false);
    }

}
