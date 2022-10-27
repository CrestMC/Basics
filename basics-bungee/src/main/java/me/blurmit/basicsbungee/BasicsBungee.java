package me.blurmit.basicsbungee;

import me.blurmit.basicsbungee.configuration.ConfigManager;
import me.blurmit.basicsbungee.listener.PluginMessageListener;
import me.blurmit.basicsbungee.listener.PlayerConnectionListener;
import me.blurmit.basicsbungee.placeholder.PlayerPlaceholder;
import net.md_5.bungee.api.plugin.Plugin;

public final class BasicsBungee extends Plugin {

    private ConfigManager configManager;

    @Override
    public void onEnable() {
        getLogger().info("Loading configuration files...");
        this.configManager = new ConfigManager(this);

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

    public ConfigManager getConfigManager() {
        return configManager;
    }

}
