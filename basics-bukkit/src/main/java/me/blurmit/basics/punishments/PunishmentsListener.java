package me.blurmit.basics.punishments;

import me.blurmit.basics.Basics;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.TimeUtil;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class PunishmentsListener implements Listener {

    private final Basics plugin;

    public PunishmentsListener(Basics plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        PunishmentManager pManager = plugin.getPunishmentManager();
        boolean allowedBanJoin = plugin.getConfigManager().getConfig().getBoolean("Punishments.Ban-Allow-Join");
        UUID uuid = event.getUniqueId();

        if (pManager.isMuted(uuid)) {
            long expiresAt = pManager.getMuteDuration(uuid);
            long timeLeft = expiresAt - TimeUtil.getCurrentTimeSeconds();
            BukkitTask task = plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> pManager.storeUnmute(uuid, null, "Expired"), timeLeft * 20L);
            pManager.getMutedPlayers().put(uuid, task);
        }

        if (pManager.isBanned(uuid)) {
            String banMessage;
            long expiresAt = pManager.getBanDuration(uuid);
            if (expiresAt == -1) {
                banMessage = Messages.BAN_PERMANENT_ALERT + "";
            } else {
                banMessage = Messages.BAN_TEMPORARY_ALERT + "";
            }

            String banReason = Placeholders.parsePlaceholder(banMessage, true, pManager.getBanReason(uuid), TimeUtil.getHowLongUntil(expiresAt));

            if (!allowedBanJoin) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, banReason);
                return;
            }

            long timeLeft = expiresAt - TimeUtil.getCurrentTimeSeconds();
            BukkitTask task = plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> pManager.storeUnban(uuid, null, "Expired"), timeLeft * 20L);

            plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                Player player = plugin.getServer().getPlayer(uuid);

                if (player == null) {
                    return;
                }

                player.sendMessage(banReason);
                pManager.getBannedPlayers().put(uuid, task);
            }, 10L);
            return;
        }

        if (!(pManager.isBlacklisted(event.getAddress().getHostAddress()) || pManager.isBlacklisted(uuid))) {
            return;
        }

        String blacklistReasonRaw = pManager.getBlacklistReason(event.getAddress().getHostAddress());
        if (blacklistReasonRaw == null) {
            blacklistReasonRaw = pManager.getBlacklistReason(uuid);
        }

        String blacklistMessage;
        long expiresAt = pManager.getBlacklistDuration(uuid);
        if (expiresAt == -1) {
            blacklistMessage = Messages.BLACKLIST_PERMANENT_ALERT + "";
        } else {
            blacklistMessage = Messages.BLACKLIST_TEMPORARY_ALERT + "";
        }

        String blacklistReason = Placeholders.parsePlaceholder(blacklistMessage, true, blacklistReasonRaw, TimeUtil.getHowLongUntil(expiresAt));

        if (!allowedBanJoin) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, blacklistReason);
            return;
        }

        long timeLeft = expiresAt - TimeUtil.getCurrentTimeSeconds();
        BukkitTask task = plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> pManager.storeUnban(uuid, null, "Expired"), timeLeft * 20L);

        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            Player player = plugin.getServer().getPlayer(uuid);

            if (player == null) {
                return;
            }

            player.sendMessage(blacklistReason);
            pManager.getBannedPlayers().put(uuid, task);
        }, 10L);
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        PunishmentManager pManager = plugin.getPunishmentManager();

        if (event.getMessage().length() == 1) {
            return;
        }

        String command = event.getMessage().split("/")[1].split(" ")[0];
        if (pManager.getBannedPlayers().containsKey(event.getPlayer().getUniqueId())) {
            if (!plugin.getConfigManager().getConfig().getStringList("Punishments.Ban-Allowed-Commands").contains(command)) {
                event.getPlayer().sendMessage(Placeholders.parsePlaceholder(Messages.COMMAND_NOT_ALLOWED_BANNED + "", command));
                event.setCancelled(true);
                return;
            }
        }

        if (pManager.getMutedPlayers().containsKey(event.getPlayer().getUniqueId())) {
            if (plugin.getConfigManager().getConfig().getStringList("Punishments.Mute-Blocked-Commands").contains(command)) {
                event.getPlayer().sendMessage(Placeholders.parsePlaceholder(Messages.COMMAND_NOT_ALLOWED_MUTED + "", command));
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        PunishmentManager pManager = plugin.getPunishmentManager();
        Player player = event.getPlayer();

        if (event.isCancelled()) {
            return;
        }

        if (pManager.getMutedPlayers().containsKey(player.getUniqueId())) {
            player.sendMessage(Placeholders.parsePlaceholder(Messages.CANT_USE_CHAT_MUTED + "", true));
            event.setCancelled(true);
            return;
        }

        if (pManager.getBannedPlayers().containsKey(player.getUniqueId())) {
            player.sendMessage(Placeholders.parsePlaceholder(Messages.CANT_USE_CHAT_BANNED + "", true));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PunishmentManager pManager = plugin.getPunishmentManager();
        UUID uuid = event.getPlayer().getUniqueId();

        if (pManager.getBannedPlayers().containsKey(uuid)) {
            pManager.getBannedPlayers().get(uuid).cancel();
            pManager.getBannedPlayers().remove(uuid);
        }

        if (pManager.getFrozenPlayers().containsKey(uuid)) {
            pManager.getFrozenPlayers().get(uuid).cancel();
            pManager.getFrozenPlayers().remove(uuid);
        }

        if (pManager.getMutedPlayers().containsKey(uuid)) {
            pManager.getMutedPlayers().get(uuid).cancel();
            pManager.getMutedPlayers().remove(uuid);
        }
    }

}
