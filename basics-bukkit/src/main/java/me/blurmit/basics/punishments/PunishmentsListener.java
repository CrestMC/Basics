package me.blurmit.basics.punishments;

import me.blurmit.basics.Basics;
import me.blurmit.basics.util.lang.Messages;
import me.blurmit.basics.util.placeholder.Placeholders;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

public class PunishmentsListener implements Listener {

    private final Basics plugin;

    public PunishmentsListener(Basics plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (plugin.getPunishmentManager().isBanned(event.getUniqueId())) {
                plugin.getServer().getPlayer(event.getUniqueId()).sendMessage(Placeholders.parsePlaceholder(
                        Messages.BAN_PERMANENT_ALERT + "",
                        plugin.getPunishmentManager().getBanReason(event.getUniqueId()))
                );
                plugin.getPunishmentManager().getBannedPlayers().add(event.getUniqueId());
            }
        }, 20);

        if (plugin.getPunishmentManager().isMuted(event.getUniqueId())) {
            plugin.getPunishmentManager().getBannedPlayers().add(event.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().length() == 1) {
            return;
        }

        String command = event.getMessage().split("/")[1].split(" ")[0];
        if (plugin.getPunishmentManager().getBannedPlayers().contains(event.getPlayer().getUniqueId())) {
            if (!plugin.getConfigManager().getConfig().getStringList("Punishments.Ban-Allowed-Commands").contains(command)) {
                event.getPlayer().sendMessage(Placeholders.parsePlaceholder(Messages.COMMAND_NOT_ALLOWED_BANNED + "", command));
                event.setCancelled(true);
            }
        }

        if (plugin.getPunishmentManager().getMutedPlayers().contains(event.getPlayer().getUniqueId())) {
            if (plugin.getConfigManager().getConfig().getStringList("Punishments.Mute-Blocked-Commands").contains(command)) {
                event.getPlayer().sendMessage(Placeholders.parsePlaceholder(Messages.COMMAND_NOT_ALLOWED_MUTED + "", command));
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (plugin.getPunishmentManager().getMutedPlayers().contains(event.getPlayer().getUniqueId())) {
            event.getPlayer().sendMessage(Placeholders.parsePlaceholder(Messages.CANT_USE_CHAT_MUTED + ""));
            event.setCancelled(true);
        }

        if (plugin.getPunishmentManager().getBannedPlayers().contains(event.getPlayer().getUniqueId())) {
            event.getPlayer().sendMessage(Placeholders.parsePlaceholder(Messages.CANT_USE_CHAT_BANNED + ""));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getPunishmentManager().getBannedPlayers().remove(event.getPlayer().getUniqueId());
        plugin.getPunishmentManager().getFrozenPlayers().remove(event.getPlayer().getUniqueId());
        plugin.getPunishmentManager().getMutedPlayers().remove(event.getPlayer().getUniqueId());
    }

}
