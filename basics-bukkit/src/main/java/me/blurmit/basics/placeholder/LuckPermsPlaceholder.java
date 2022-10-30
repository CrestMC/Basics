package me.blurmit.basics.placeholder;

import me.blurmit.basics.Basics;
import me.blurmit.basics.events.PlaceholderRequestEvent;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LuckPermsPlaceholder implements Listener {

    private final Basics plugin;

    public LuckPermsPlaceholder(Basics plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlaceholderRequest(PlaceholderRequestEvent event) {
        String placeholder = event.getPlaceholder().toLowerCase();

        if (placeholder.equalsIgnoreCase("luckperms-prefix")) {
            try {
                User user = LuckPermsProvider.get().getPlayerAdapter(Player.class).getUser(event.getPlayer());
                event.setResponse(user.getCachedData().getMetaData().getPrefix());
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.equalsIgnoreCase("luckperms-suffix")) {
            try {
                User user = LuckPermsProvider.get().getPlayerAdapter(Player.class).getUser(event.getPlayer());
                event.setResponse(user.getCachedData().getMetaData().getSuffix());
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.startsWith("luckperms-meta-")) {
            try {
                String metakey = placeholder.replaceFirst("luckperms-meta-", "");
                User user = LuckPermsProvider.get().getPlayerAdapter(Player.class).getUser(event.getPlayer());
                event.setResponse(user.getCachedData().getMetaData().getMetaValue(metakey));
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.equalsIgnoreCase("luckperms-primary-group")) {
            try {
                User user = LuckPermsProvider.get().getPlayerAdapter(Player.class).getUser(event.getPlayer());
                event.setResponse(user.getCachedData().getMetaData().getPrimaryGroup());
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.equalsIgnoreCase("luckperms-primary-group")) {
            try {
                User user = LuckPermsProvider.get().getPlayerAdapter(Player.class).getUser(event.getPlayer());
                event.setResponse(user.getCachedData().getMetaData().getPrimaryGroup());
            } catch (Exception e) {
                event.setResponse("");
            }
        }
    }

}
