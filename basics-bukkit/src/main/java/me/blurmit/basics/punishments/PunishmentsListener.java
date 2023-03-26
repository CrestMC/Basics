package me.blurmit.basics.punishments;

import me.blurmit.basics.Basics;
import me.blurmit.basics.punishments.data.PunishmentData;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.TimeUtil;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.UUID;

public class PunishmentsListener implements Listener {

    private final Basics plugin;
    private final boolean banAllowJoin;

    public PunishmentsListener(Basics plugin) {
        this.plugin = plugin;
        this.banAllowJoin = plugin.getConfigManager().getConfig().getBoolean("Punishments.Ban-Allow-Join");

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        PunishmentManager punishmentManager = plugin.getPunishmentManager();
        UUID target = event.getUniqueId();

        if (punishmentManager.isBanned(target)) {
            handleBanJoin(target, event, punishmentManager);
            return;
        }

        if (punishmentManager.isBlacklisted(target)) {
            handleBlacklistJoin(target, event, punishmentManager);
            return;
        }

        if (punishmentManager.isMuted(target)) {
            handleMuteJoin(target, punishmentManager);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {

    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        PunishmentManager punishmentManager = plugin.getPunishmentManager();
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (message.length() == 1) {
            return;
        }

        List<String> muteBlockedCommands = plugin.getConfigManager().getConfig().getStringList("Punishments.Mute-Blocked-Commands");
        List<String> banAllowedCommands = plugin.getConfigManager().getConfig().getStringList("Punishments.Ban-Allowed-Commands");
        String command = message.split("/")[1].split(" ")[0];

        if (punishmentManager.getBannedPlayers().containsKey(player.getUniqueId())) {
            if (banAllowedCommands.contains(command)) {
                return;
            }

            player.sendMessage(Placeholders.parse(Messages.COMMAND_NOT_ALLOWED_BANNED + "", command));
            event.setCancelled(true);
            return;
        }

        if (punishmentManager.getMutedPlayers().containsKey(event.getPlayer().getUniqueId())) {
            if (!muteBlockedCommands.contains(command)) {
                return;
            }

            player.sendMessage(Placeholders.parse(Messages.COMMAND_NOT_ALLOWED_MUTED + "", command));
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        PunishmentManager punishmentManager = plugin.getPunishmentManager();
        Player player = event.getPlayer();

        if (punishmentManager.getMutedPlayers().containsKey(player.getUniqueId())) {
            player.sendMessage(Placeholders.parse(Messages.CANT_USE_CHAT_MUTED + "", true));
            event.setCancelled(true);
            return;
        }

        if (punishmentManager.getBannedPlayers().containsKey(player.getUniqueId())) {
            player.sendMessage(Placeholders.parse(Messages.CANT_USE_CHAT_BANNED + "", true));
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

    private void handleBlacklistJoin(UUID target, AsyncPlayerPreLoginEvent event, PunishmentManager punishmentManager) {
        PunishmentData data = punishmentManager.getPunishmentData(PunishmentType.BLACKLIST, target);
        long expiresAt = data.getExpiresAt();
        BukkitTask task = null;
        if (expiresAt != -1) {
            task = runPunishmentTask(data.getExpiresAt(), () -> punishmentManager.storeUnblacklist(target, null, "Expired"));
        }
        punishmentManager.getBannedPlayers().put(target, task);

        String expiresAtText = TimeUtil.getHowLongUntil(expiresAt);
        String message = Messages.BLACKLIST_PERMANENT_JOIN + "";
        String reason = data.getReason();

        if (expiresAt != -1) {
            message = Messages.BLACKLIST_TEMPORARY_JOIN + "";
        }

        message = Placeholders.parse(message, true, reason, expiresAtText);
        if (!banAllowJoin) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, message);
        }

        String alertMessage = message;
        runPunishmentTask(TimeUtil.getCurrentTimeSeconds() + 1, () -> {
            Player player = plugin.getServer().getPlayer(target);
            if (player != null) {
                player.sendMessage(alertMessage);
            }
        });
    }

    private void handleBanJoin(UUID target, AsyncPlayerPreLoginEvent event, PunishmentManager punishmentManager) {
        PunishmentData data = punishmentManager.getPunishmentData(PunishmentType.BAN, target);
        long expiresAt = data.getExpiresAt();
        BukkitTask task = null;
        if (expiresAt != -1) {
            task = runPunishmentTask(expiresAt, () -> punishmentManager.storeUnban(target, null, "Expired"));
        }
        punishmentManager.getBannedPlayers().put(target, task);

        String expiresAtText = TimeUtil.getHowLongUntil(expiresAt);
        String message = Messages.BAN_PERMANENT_JOIN + "";
        String reason = data.getReason();

        if (expiresAt != -1) {
            message = Messages.BAN_TEMPORARY_JOIN + "";
        }

        message = Placeholders.parse(message, true, reason, expiresAtText);
        if (!banAllowJoin) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, message);
        }

        String alertMessage = message;
        runPunishmentTask(TimeUtil.getCurrentTimeSeconds() + 1, () -> {
            Player player = plugin.getServer().getPlayer(target);
            if (player != null) {
                player.sendMessage(alertMessage);
            }
        });
    }

    private void handleMuteJoin(UUID target, PunishmentManager punishmentManager) {
        PunishmentData data = punishmentManager.getPunishmentData(PunishmentType.MUTE, target);
        BukkitTask task = null;
        if (data.getPunishedAt() != -1) {
            task = runPunishmentTask(data.getPunishedAt(), () -> punishmentManager.storeUnmute(target, null, "Expired"));
        }
        punishmentManager.getMutedPlayers().put(target, task);
    }

    private BukkitTask runPunishmentTask(long expiresAt, Runnable task) {
        long timeLeft = expiresAt - TimeUtil.getCurrentTimeSeconds();
        BukkitScheduler scheduler = plugin.getServer().getScheduler();
        return scheduler.runTaskLaterAsynchronously(plugin, task, timeLeft * 20L);
    }

}
