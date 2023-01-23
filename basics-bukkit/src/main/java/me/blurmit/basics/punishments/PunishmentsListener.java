package me.blurmit.basics.punishments;

import me.blurmit.basics.Basics;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PunishmentsListener implements Listener {

    private final Basics plugin;

    public PunishmentsListener(Basics plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if (plugin.getPunishmentManager().isMuted(event.getUniqueId())) {
            plugin.getPunishmentManager().getMutedPlayers().add(event.getUniqueId());
        }

        if (plugin.getPunishmentManager().isBanned(event.getUniqueId())) {
            String banReason = Placeholders.parsePlaceholder(Messages.BAN_PERMANENT_JOIN + "", true, plugin.getPunishmentManager().getBanReason(event.getUniqueId()));

            if (!plugin.getConfigManager().getConfig().getBoolean("Punishments.Ban-Allow-Join")) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, banReason);
                return;
            }

            plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                Player player = plugin.getServer().getPlayer(event.getUniqueId());

                if (player == null) {
                    return;
                }

                player.sendMessage(banReason);
                plugin.getPunishmentManager().getBannedPlayers().add(event.getUniqueId());
            }, 10L);

            return;
        }

        if (plugin.getPunishmentManager().isBlacklisted(event.getAddress().getHostAddress())) {
            String blacklistReason = Placeholders.parsePlaceholder(Messages.BLACKLIST_PERMANENT_JOIN + "", true, plugin.getPunishmentManager().getBlacklistReason(event.getAddress().getHostAddress()));

            if (!plugin.getConfigManager().getConfig().getBoolean("Punishments.Ban-Allow-Join")) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, blacklistReason);
                return;
            }

            plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                Player player = plugin.getServer().getPlayer(event.getUniqueId());

                if (player == null) {
                    return;
                }

                player.sendMessage(blacklistReason);
                plugin.getPunishmentManager().getBannedPlayers().add(event.getUniqueId());
            }, 10L);
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        event.getPlayer().getLocation();

        if (event.isCancelled()) {
            return;
        }

        if (plugin.getPunishmentManager().getMutedPlayers().contains(event.getPlayer().getUniqueId())) {
            event.getPlayer().sendMessage(Placeholders.parsePlaceholder(Messages.CANT_USE_CHAT_MUTED + "", true));
            event.setCancelled(true);
            return;
        }

        if (plugin.getPunishmentManager().getBannedPlayers().contains(event.getPlayer().getUniqueId())) {
            event.getPlayer().sendMessage(Placeholders.parsePlaceholder(Messages.CANT_USE_CHAT_BANNED + "", true));
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
