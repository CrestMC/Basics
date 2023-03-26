package me.blurmit.basics.rank.storage.provider;

import me.blurmit.basics.Basics;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class RankStorageProvider {

    private static final Map<Class<? extends RankStorageProvider>, RankStorageProvider> providers = new HashMap<>();

    static {
        Basics plugin = JavaPlugin.getPlugin(Basics.class);

        providers.put(RankYamlStorage.class, new RankYamlStorage(plugin));
        providers.put(RankSQLStorage.class, new RankSQLStorage(plugin));
    }

    public static RankStorageProvider getProvider(Class<? extends RankStorageProvider> provider) {
        return providers.get(provider);
    }

    /**
     * Called when the storage provider is ready to be loaded.
     * <p>Any necessary database components should be created here.</p>
     */
    public abstract void load();

    /**
     * Stores a rank update to the specified storage provider.
     * @param target     Target to receive the rank
     * @param issuer     User who gave the rank
     * @param reason     Reason why the rank is being given (can be null)
     * @param givenAt    The unix-epoch time stamp that this rank was given
     * @param expiresAt  The unix-epoch time stamp that this rank will expire (use -1 to never expire)
     * @param server     The server this rank was given on
     */
    public abstract void storeGiveRank(UUID target, UUID issuer, String reason, long givenAt, long expiresAt, String server);

    /**
     * Stores a rank update to the specified storage provider.
     * @param target     Target to receive the rank
     * @param issuer     User who gave the rank
     * @param reason     Reason why the rank is being given (can be null)
     * @param removedAt  The unix-epoch time stamp that this rank was removed at
     * @param server     The server this rank was given on
     */
    public abstract void storeRemoveRank(UUID target, UUID issuer, String reason, long removedAt, String server);

    /**
     * Stores a newly-created rank to the specified storage provider
     * @param name The name of the rank to be created
     * @param server The server that the rank should be created on (use null for all servers)
     */
    public abstract void storeRankCreate(String name, String server);

    /**
     * Stores a rank removal to the specified storage provider
     * @param name The name of the rank to be deleted
     * @param server The server that the rank should be removed from (use null for all servers)
     */
    public abstract void storeRankDelete(String name, String server);

    /**
     * Stores adding a rank permission to the specified storage provider
     * @param rank Name of the rank that the permission will be added to
     * @param permission The name of the rank that the permission will be removed from
     * @param value Whether the permission value should be true or false
     * @param server The server this permission should be given on (use null for all servers)
     */
    public abstract void storePermissionRemove(String rank, String permission, boolean value, String server);

    /**
     * Stores adding a rank permission to the specified storage provider
     * @param rank The name of the rank that the permission will be added to
     * @param permission The permission node that should be removed from the rank
     * @param server The server this permission should be removed from (use null for all servers)
     */
    public abstract void storePermissionAdd(String rank, String permission, String server);

    /**
     * Stores a rank prefix update to the specified storage provider
     * @param rank The name of the rank that the prefix will be updated on
     * @param prefix The new prefix
     */
    public abstract void storePrefixUpdate(String rank, String prefix);


    /**
     * Stores a rank suffix update to the specified storage provider
     * @param rank The name of the rank that the suffix will be updated on
     * @param suffix The new suffix
     */
    public abstract void storeSuffixUpdate(String rank, String suffix);

    /**
     * Stores a rank color update to the specified storage provider
     * @param rank The name of the rank that the color will be updated on
     * @param color The new color
     */
    public abstract void storeColorUpdate(String rank, String color);

    /**
     * Stores a rank default state update to the specified storage provider
     * @param rank The name of the rank that the default state will be updated on
     * @param def The new state
     */
    public abstract void storeDefaultUpdate(String rank, boolean def);

    /**
     * Stores a rank display name update to the specified storage provider
     * @param rank The name of the rank that the display name will be updated on
     * @param displayName The new display name
     */
    public abstract void storeDisplayNameUpdate(String rank, String displayName);

    /**
     * Stores a rank priority update to the specified storage provider
     * @param rank The name of the rank that the priority will be updated on
     * @param priority The new priority
     */
    public abstract void storePriorityUpdate(String rank, int priority);

    /**
     * Stores a rank metadata update to the specified storage provider
     * @param rank The name of the rank that the metadata will be updated on
     * @param metadata The metadata to be updated
     */
    public abstract void storeMetadataUpdate(String rank, Object metadata);
}
