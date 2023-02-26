package me.blurmit.basics.punishments.storage.provider;

import lombok.Getter;
import me.blurmit.basics.Basics;
import me.blurmit.basics.punishments.PunishmentType;
import me.blurmit.basics.punishments.data.PunishmentData;
import me.blurmit.basics.punishments.storage.provider.PunishmentStorageProvider;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class PunishmentYamlStorage extends PunishmentStorageProvider {

    private final Basics plugin;

    @Getter
    private FileConfiguration storage;
    private final File punishmentsFile;

    public PunishmentYamlStorage(Basics plugin) {
        this.plugin = plugin;

        this.punishmentsFile = new File(plugin.getDataFolder(), "punishments.yml");
    }

    /**
     * Called when the storage provider is ready to be loaded.
     * <p>Any necessary database components should be loaded here.</p>
     */
    @Override
    public void load() {
        if (punishmentsFile.exists()) {
            return;
        }

        plugin.saveResource(punishmentsFile.getName(), false);
        storage = YamlConfiguration.loadConfiguration(punishmentsFile);
    }

    private void save() {
        try {
            storage.save(punishmentsFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred whilst saving the punishments storage file", e);
        }
    }

    /**
     * Stores a ban in the specified storage provider
     *
     * @param target    Target to be banned
     * @param moderator Moderator who issued the ban
     * @param reason    Reason why the punishment is being issued
     * @param punishedAt The unix-epoch time stamp that this punishment was made
     * @param expiresAt The unix-epoch time stamp that this punishment will expire
     * @param server    The server this punishment was issued on
     */
    @Override
    public void storeBan(UUID target, UUID moderator, String reason, long punishedAt, long expiresAt, String server) {
        // Delete current ban from database if it exists.
        if (storage.getConfigurationSection("bans." + target) != null) {
            storeUnban(target, moderator, "Ban Override", server);
        }

        storage.set("bans." + target + ".moderator", moderator == null ? "null" : moderator.toString());
        storage.set("bans." + target + ".punished-at", punishedAt);
        storage.set("bans." + target + ".expires-at", expiresAt);
        storage.set("bans." + target + ".reason", reason);
        storage.set("bans." + target + ".server", server);
        save();
    }

    /**
     * Stores an unban in the specified storage provider
     *
     * @param target    Target to be unbanned
     * @param moderator Moderator who issued to unban
     * @param reason    Reason why to unban
     * @param server    The server this specific unban was issued on
     */
    @Override
    public void storeUnban(UUID target, UUID moderator, String reason, String server) {
        if (storage.getConfigurationSection("bans." + target) == null) {
            return;
        }

        storage.set("bans." + target, null);
        save();
    }

    /**
     * Stores a blacklist in the specified storage provider
     *
     * @param target    Target to be banned
     * @param moderator Moderator who issued the ban
     * @param reason    Reason why the punishment is being issued
     * @param punishedAt The unix-epoch time stamp that this punishment was made
     * @param expiresAt The unix-epoch time stamp that this punishment will expire
     * @param server    The server this punishment was issued on
     */
    @Override
    public void storeBlacklist(UUID target, UUID moderator, String reason, long punishedAt, long expiresAt, String server) {
        // Delete current blacklist from database if it exists.
        if (storage.getConfigurationSection("blacklists." + target) != null) {
            storeUnblacklist(target, moderator, "Ban Override", server);
        }

        storage.set("blacklists." + target + ".ip", "0.0.0.0");
        storage.set("blacklists." + target + ".moderator", moderator == null ? "null" : moderator + "");
        storage.set("blacklists." + target + ".punished-at", punishedAt);
        storage.set("blacklists." + target + ".expires-at", expiresAt);
        storage.set("blacklists." + target + ".reason", reason);
        storage.set("blacklists." + target + ".server", server);
        save();
    }

    /**
     * Stores an un-blacklist in the specified storage provider
     *
     * @param target    Target to be un-blacklisted
     * @param moderator Moderator who issued the un-blacklist
     * @param reason    Reason to unban
     * @param server    The server this un-blacklist was issued on
     */
    @Override
    public void storeUnblacklist(UUID target, UUID moderator, String reason, String server) {
        ConfigurationSection blacklistSection = storage.getConfigurationSection("blacklists");
        blacklistSection.getValues(false).forEach((player, punishment_data) -> {
            ConfigurationSection punishmentData = (ConfigurationSection) punishment_data;

            if (punishmentData.getString("ip").equals("0.0.0.0")) {
                blacklistSection.set(player, null);
            }
        });
    }

    /**
     * Stores a mute in the specified storage provider
     *
     * @param target    Target to be banned
     * @param moderator Moderator who issued the ban
     * @param reason    Reason why the punishment is being issued
     * @param punishedAt The unix-epoch time stamp that this punishment was made
     * @param expiresAt The unix-epoch time stamp that this punishment will expire
     * @param server    The server this punishment was issued on
     */
    @Override
    public void storeMute(UUID target, UUID moderator, String reason, long punishedAt, long expiresAt, String server) {
        // Delete current mute from database if it exists.
        if (storage.getConfigurationSection("mutes." + target) != null) {
            storeUnmute(target, moderator, "Mute Override", server);
        }

        storage.set("mutes." + target + ".moderator", moderator == null ? "null" : moderator + "");
        storage.set("mutes." + target + ".punished-at", punishedAt);
        storage.set("mutes." + target + ".expires-at", expiresAt);
        storage.set("mutes." + target + ".reason", reason);
        storage.set("mutes." + target + ".server", server);
        save();
    }

    /**
     * Stores an unmute in the specified storage provider
     *
     * @param target    Target to be unmuted
     * @param moderator Moderator who issued the unmute
     * @param reason    Reason to unmute
     * @param server    The server this unmute was issued on
     */
    @Override
    public void storeUnmute(UUID target, UUID moderator, String reason, String server) {
        if (storage.getConfigurationSection("mutes." + target) == null) {
            return;
        }

        storage.set("mutes." + target, null);
        save();
    }

    /**
     * Stores a punishment in the specified storage provider
     *
     * @param type      The punishment type to be issued
     * @param target    Target to be banned
     * @param moderator Moderator who issued the ban
     * @param reason    Reason why the punishment is being issued
     * @param expiresAt The unix-epoch time stamp that this punishment will expire
     * @param server    The server this punishment was issued on
     */
    @Override
    public void storePunishment(PunishmentType type, UUID target, UUID moderator, String reason, long punishedAt, long expiresAt, String server) {
        ConfigurationSection historySection = storage.getConfigurationSection("history." + target);
        int id = historySection == null ? 0 : historySection.getValues(false).size() + 1;

        while (historySection != null && historySection.get(id + "") != null) {
            id++;
        }

        historySection.set(id + ".punishment", type.name());
        historySection.set(id + ".moderator", moderator);
        historySection.set(id + ".punished-at", punishedAt);
        historySection.set(id + ".expires-at", expiresAt);
        historySection.set(id + ".server", server);
        historySection.set(id + ".reason", reason);
        save();
    }

    /**
     * Gets the target's punishment data directly from the specified storage provider for the {@link PunishmentType} provided.
     * <p>If the target has no active punishments of that type, this will return null.</p>
     * <p>The {@link CompletableFuture < PunishmentData >} will always be completed, regardless of the target's punishment state.</p>
     *
     * @param type   The punishment type that should be looked up.
     * @param target The target in which the data will be derived from.
     * @return A {@link CompletableFuture<PunishmentData>} containing all data about the {@link PunishmentType} provided.
     */
    @Override
    public CompletableFuture<PunishmentData> getPunishmentData(PunishmentType type, UUID target) {
        return CompletableFuture.supplyAsync(() -> {
            ConfigurationSection punishmentSection = storage.getConfigurationSection(type + "." + target);
            if (punishmentSection == null) {
                return null;
            }

            return PunishmentData.of(punishmentSection);
        });
    }

}
