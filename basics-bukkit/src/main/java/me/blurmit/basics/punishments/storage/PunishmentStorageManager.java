package me.blurmit.basics.punishments.storage;

import lombok.Getter;
import me.blurmit.basics.Basics;
import me.blurmit.basics.punishments.storage.provider.PunishmentStorageProvider;

public class PunishmentStorageManager {

    @Getter
    private final PunishmentStorageType storageType;
    @Getter
    private final PunishmentStorageProvider storageProvider;

    public PunishmentStorageManager(Basics plugin) {
        String storageProviderName = plugin.getConfigManager().getConfig().getString("Punishments.Storage-Method");
        storageType = PunishmentStorageType.getByName(storageProviderName);
        storageProvider = storageType.getProvider();
        storageProvider.load();

        plugin.getLogger().info("Using " + storageType.name() + " as storage provider.");
    }

}
