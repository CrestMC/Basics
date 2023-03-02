package me.blurmit.basics.rank.storage;

import me.blurmit.basics.rank.storage.provider.RankSQLStorage;
import me.blurmit.basics.rank.storage.provider.RankStorageProvider;
import me.blurmit.basics.rank.storage.provider.RankYamlStorage;

import java.util.HashMap;
import java.util.Map;

public enum RankStorageType {

    YAML() {
        /**
         * @return The {@link RankStorageProvider} associated with this storage type
         */
        @Override
        public RankStorageProvider getProvider() {
            return RankStorageProvider.getProvider(RankYamlStorage.class);
        }

        /**
         * @return All possible aliases that can be used to retrieve this particular storage type
         */
        @Override
        public String[] getAliases() {
            return new String[] { "config", "configuration", "yml", "yaml", "file" };
        }
    },
    MYSQL() {
        /**
         * @return The {@link RankStorageProvider} associated with this storage type
         */
        @Override
        public RankStorageProvider getProvider() {
            return RankStorageProvider.getProvider(RankSQLStorage.class);
        }

        /**
         * @return All possible aliases that can be used to retrieve this particular storage type
         */
        @Override
        public String[] getAliases() {
            return new String[] { "mysql", "sql" };
        }
    };

    public abstract RankStorageProvider getProvider();
    public abstract String[] getAliases();

    private static final Map<String, RankStorageType> types = new HashMap<>();

    static {
        for (RankStorageType type : values()) {
            for (String alias : type.getAliases()) {
                types.put(alias, type);
            }
        }
    }

    /**
     * Gets a {@link RankStorageType} instance by the name specified.
     * <p>
     * The name must be defined as it is in the {@link RankStorageType#getAliases()}
     * </p>
     * @param name The string that will be used to retrieve a {@link RankStorageType} instance
     * @return A {@link RankStorageType} instance
     */
    public static RankStorageType getByName(String name) {
        return types.get(name);
    }

}
