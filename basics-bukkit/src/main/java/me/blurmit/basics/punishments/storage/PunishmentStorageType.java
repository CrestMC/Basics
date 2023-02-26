package me.blurmit.basics.punishments.storage;

import me.blurmit.basics.punishments.storage.provider.PunishmentSQLStorage;
import me.blurmit.basics.punishments.storage.provider.PunishmentStorageProvider;
import me.blurmit.basics.punishments.storage.provider.PunishmentYamlStorage;

import java.util.HashMap;
import java.util.Map;

public enum PunishmentStorageType {

    YAML() {
        /**
         * @return The {@link PunishmentStorageProvider} associated with this storage type
         */
        @Override
        public PunishmentStorageProvider getProvider() {
            return PunishmentStorageProvider.getProvider(PunishmentYamlStorage.class);
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
         * @return The {@link PunishmentStorageProvider} associated with this storage type
         */
        @Override
        public PunishmentStorageProvider getProvider() {
            return PunishmentStorageProvider.getProvider(PunishmentSQLStorage.class);
        }

        /**
         * @return All possible aliases that can be used to retrieve this particular storage type
         */
        @Override
        public String[] getAliases() {
            return new String[] { "mysql", "sql" };
        }
    };

    public abstract PunishmentStorageProvider getProvider();
    public abstract String[] getAliases();

    private static final Map<String, PunishmentStorageType> types = new HashMap<>();

    static {
        for (PunishmentStorageType type : values()) {
            for (String alias : type.getAliases()) {
                types.put(alias, type);
            }
        }
    }

    /**
     * Gets a {@link PunishmentStorageType} instance by the name specified.
     * <p>
     * The name must be defined as it is in the {@link PunishmentStorageType#getAliases()}
     * </p>
     * @param name The string that will be used to retrieve a {@link PunishmentStorageType} instance
     * @return A {@link PunishmentStorageType} instance
     */
    public static PunishmentStorageType getByName(String name) {
        return types.get(name);
    }

}
