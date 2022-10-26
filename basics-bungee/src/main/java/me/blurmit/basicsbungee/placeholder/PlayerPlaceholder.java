package me.blurmit.basicsbungee.placeholder;

import me.blurmit.basicsbungee.BasicsBungee;
import me.blurmit.basicsbungee.event.PlaceholderRequestEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerPlaceholder implements Listener {

    private final BasicsBungee plugin;

    public PlayerPlaceholder(BasicsBungee plugin) {
        this.plugin = plugin;
        plugin.getProxy().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onPlaceholderRequest(PlaceholderRequestEvent event) {
        String placeholder = event.getPlaceholder().toLowerCase();

        switch (placeholder) {
            case "player-name": {
                try {
                    event.setResponse(event.getPlayer().getName());
                } catch (Exception e) {
                    event.setResponse("");
                }
                break;
            }
            case "player-server": {
                try {
                    event.setResponse(event.getPlayer().getServer().getInfo().getName());
                } catch (Exception e) {
                    event.setResponse("");
                }
                break;
            }
            case "player-ping": {
                try {
                    event.setResponse(event.getPlayer().getPing() + "");
                } catch (Exception e) {
                    event.setResponse("");
                }
                break;
            }
            case "player-uuid": {
                try {
                    event.setResponse(event.getPlayer().getUniqueId().toString());
                } catch (Exception e) {
                    event.setResponse("");
                }
                break;
            }
        }
    }

}
