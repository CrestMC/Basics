package me.blurmit.basicsbungee;

import me.blurmit.basicsbungee.command.ServerAliasCommand;
import me.blurmit.basicsbungee.configuration.ConfigManager;
import me.blurmit.basicsbungee.listener.PluginMessageListener;
import me.blurmit.basicsbungee.listener.PlayerConnectionListener;
import me.blurmit.basicsbungee.placeholder.PlayerPlaceholder;
import net.md_5.bungee.api.plugin.Plugin;

public final class BasicsBungee extends Plugin {

    private static ConfigManager configManager;

    @Override
    public void onEnable() {
        getLogger().info("Loading configuration files...");
        configManager = new ConfigManager(this);

        getLogger().info("Registering commands...");
        configManager.getConfig().getSection("Server-Aliases").getKeys().forEach(key -> {
            getProxy().getPluginManager().registerCommand(
                    this,
                    new ServerAliasCommand(this, key, configManager.getConfig().getSection("Server-Aliases").getString(key))
            );
        });

        getLogger().info("Registering listeners...");
        new PluginMessageListener(this);
        new PlayerConnectionListener(this);

        getLogger().info("Loading placeholders...");
        new PlayerPlaceholder(this);

        getLogger().info(getDescription().getName() + " has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info(getDescription().getName() + " has been enabled!");
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

}
