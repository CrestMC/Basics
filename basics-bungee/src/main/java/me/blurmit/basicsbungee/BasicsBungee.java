package me.blurmit.basicsbungee;

import me.blurmit.basicsbungee.command.LimboCommand;
import me.blurmit.basicsbungee.command.ServerAliasCommand;
import me.blurmit.basicsbungee.configuration.ConfigManager;
import me.blurmit.basicsbungee.limbo.LimboManager;
import me.blurmit.basicsbungee.listener.PlayerConnectionListener;
import me.blurmit.basicsbungee.listener.PluginMessageListener;
import me.blurmit.basicsbungee.listener.ProxyPingListener;
import me.blurmit.basicsbungee.placeholder.CommandPlaceholder;
import me.blurmit.basicsbungee.placeholder.PlayerPlaceholder;
import net.md_5.bungee.api.plugin.Plugin;

public final class BasicsBungee extends Plugin {

    private ConfigManager configManager;
    private LimboManager limboManager;

    private static BasicsBungee instance;

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
        getProxy().getPluginManager().registerCommand(this, new LimboCommand(this));

        getLogger().info("Loading limbo...");
        this.limboManager = new LimboManager(this);

        getLogger().info("Registering listeners...");
        new PluginMessageListener(this);
        new PlayerConnectionListener(this);
        new ProxyPingListener(this);

        getLogger().info("Loading placeholders...");
        new PlayerPlaceholder(this);
        new CommandPlaceholder(this);

        instance = this;
        getLogger().info(getDescription().getName() + " has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info(getDescription().getName() + " has been disabled!");
    }

    public static BasicsBungee getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public LimboManager getLimboManager() {
        return limboManager;
    }

}
