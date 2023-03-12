package me.blurmit.basics.rank.storage.provider;

import me.blurmit.basics.Basics;

public class RankSQLStorage extends RankStorageProvider {

    private final Basics plugin;

    public RankSQLStorage(Basics plugin) {
        this.plugin = plugin;
    }

    /**
     * Called when the storage provider is ready to be loaded.
     * <p>Any necessary database components should be created here.</p>
     */
    @Override
    public void load() {

    }

}