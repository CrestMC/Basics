package me.blurmit.basics.punishments.storage;

import lombok.Getter;
import me.blurmit.basics.Basics;
import me.blurmit.basics.database.DatabaseManager;
import me.blurmit.basics.punishments.storage.provider.PunishmentStorageProvider;

public class PunishmentStorageManager {

    private final Basics plugin;

    @Getter
    private final PunishmentStorageType storageType;

    @Getter
    private final PunishmentStorageProvider storageProvider;


    @Getter
    private DatabaseManager databaseManager;

    public PunishmentStorageManager(Basics plugin) {
        this.plugin = plugin;

        String storageProviderName = plugin.getConfigManager().getConfig().getString("Punishments.Storage-Method");
        storageType = PunishmentStorageType.getByName(storageProviderName);
        storageProvider = storageType.getProvider();
        storageProvider.load();

        plugin.getLogger().info("Using " + storageType.name() + " as storage provider.");
    }

}
