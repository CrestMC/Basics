package me.blurmit.basicsbungee.limbo;

import net.md_5.bungee.BungeeServerInfo;
import net.md_5.bungee.api.ProxyServer;

import java.net.InetSocketAddress;

public class LimboServer extends BungeeServerInfo {

    public LimboServer() {
        super(
                "Limbo",
                new InetSocketAddress("0.0.0.0", ((InetSocketAddress) ProxyServer.getInstance().getConfig().getListeners().stream().findFirst().get().getSocketAddress()).getPort()),
                "A Limbo Server",
                false
        );
    }

}
