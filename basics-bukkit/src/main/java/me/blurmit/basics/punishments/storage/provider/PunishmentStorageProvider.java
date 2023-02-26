package me.blurmit.basics.punishments.storage.provider;

import me.blurmit.basics.Basics;
import me.blurmit.basics.punishments.PunishmentType;
import me.blurmit.basics.punishments.data.PunishmentData;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class PunishmentStorageProvider {

    private static final Map<Class<? extends PunishmentStorageProvider>, PunishmentStorageProvider> providers = new HashMap<>();

    static {
        Basics plugin = JavaPlugin.getPlugin(Basics.class);

        providers.put(PunishmentYamlStorage.class, new PunishmentYamlStorage(plugin));
        providers.put(PunishmentSQLStorage.class, new PunishmentSQLStorage(plugin));
    }

    public static PunishmentStorageProvider getProvider(Class<? extends PunishmentStorageProvider> provider) {
        return providers.get(provider);
    }

    /**
     * Called when the storage provider is ready to be loaded.
     * <p>Any necessary database components should be created here.</p>
     */
    public abstract void load();

    /** Stores a ban in the specified storage provider
     * @param target Target to be banned
     * @param moderator Moderator who issued the ban
     * @param reason Reason why the punishment is being issued
     * @param punishedAt The unix-epoch time stamp that this punishment was made
     * @param expiresAt The unix-epoch time stamp that this punishment will expire
     * @param server The server this punishment was issued on
     */
    public abstract void storeBan(UUID target, UUID moderator, String reason, long punishedAt, long expiresAt, String server);

    /**
     * Stores an unban in the specified storage provider
     *
     * @param target    Target to be unbanned
     * @param moderator Moderator who issued to unban
     * @param reason    Reason why to unban
     * @param server    The server this specific unban was issued on
     */
    public abstract void storeUnban(UUID target, UUID moderator, String reason, String server);

    /** Stores a blacklist in the specified storage provider
     * @param target Target to be banned
     * @param moderator Moderator who issued the ban
     * @param reason Reason why the punishment is being issued
     * @param punishedAt The unix-epoch time stamp that this punishment was made
     * @param expiresAt The unix-epoch time stamp that this punishment will expire
     * @param server The server this punishment was issued on
     */
    public abstract void storeBlacklist(UUID target, UUID moderator, String reason, long punishedAt, long expiresAt, String server);

    /** Stores an un-blacklist in the specified storage provider
     * @param target Target to be un-blacklisted
     * @param moderator Moderator who issued the un-blacklist
     * @param reason Reason to unban
     * @param server The server this un-blacklist was issued on
     */
    public abstract void storeUnblacklist(UUID target, UUID moderator, String reason, String server);

    /** Stores a mute in the specified storage provider
     * @param target Target to be banned
     * @param moderator Moderator who issued the ban
     * @param reason Reason why the punishment is being issued
     * @param punishedAt The unix-epoch time stamp that this punishment was made
     * @param expiresAt The unix-epoch time stamp that this punishment will expire
     * @param server The server this punishment was issued on
     */
    public abstract void storeMute(UUID target, UUID moderator, String reason, long punishedAt, long expiresAt, String server);

    /** Stores an unmute in the specified storage provider
     * @param target Target to be unmuted
     * @param moderator Moderator who issued the unmute
     * @param reason Reason to unmute
     * @param server The server this unmute was issued on
     */
    public abstract void storeUnmute(UUID target, UUID moderator, String reason, String server);

    /** Stores a punishment in the specified storage provider
     * @param type The punishment type to be issued
     * @param target Target to be banned
     * @param moderator Moderator who issued the ban
     * @param reason Reason why the punishment is being issued
     * @param punishedAt The unix-epoch time stamp that this punishment was made
     * @param expiresAt The unix-epoch time stamp that this punishment will expire
     * @param server The server this punishment was issued on
     */
    public abstract void storePunishment(PunishmentType type, UUID target, UUID moderator, String reason, long punishedAt, long expiresAt, String server);

    /**
     * Gets the target's punishment data directly from the specified storage provider for the {@link PunishmentType} provided.
     * <p>If the target has no active punishments of that type, this will return null.</p>
     * <p>The {@link CompletableFuture<PunishmentData>} will always be completed, regardless of the target's punishment state.</p>
     * @param type The punishment type that should be looked up.
     * @param target The target in which the data will be derived from.
     * @return A {@link CompletableFuture<PunishmentData>} containing all data about the {@link PunishmentType} provided.
     */
    public abstract CompletableFuture<PunishmentData> getPunishmentData(PunishmentType type, UUID target);

}
