package me.blurmit.basics.rank.storage.provider;

import me.blurmit.basics.Basics;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

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

}
