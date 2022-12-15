package me.blurmit.basicsbungee.placeholder;

import me.blurmit.basicsbungee.BasicsBungee;
import me.blurmit.basicsbungee.event.PlaceholderRequestEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Arrays;

public class CommandPlaceholder implements Listener {

    private final BasicsBungee plugin;

    public CommandPlaceholder(BasicsBungee plugin) {
        this.plugin = plugin;
        plugin.getProxy().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onPlaceholderRequest(PlaceholderRequestEvent event) {
        String placeholder = event.getPlaceholder().toLowerCase();

        if (placeholder.startsWith("replacement-")) {
            try {
                int index = Integer.parseInt(placeholder.replace("replacement-", ""));
                event.setResponse(Arrays.asList(event.getReplacements()).get(index).toString());
            } catch (Exception e) {
                event.setResponse("");
            }
        }
    }

}
