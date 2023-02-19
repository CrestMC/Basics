package me.blurmit.basics;

import lombok.Getter;
import me.blurmit.basics.command.CommandManager;
import me.blurmit.basics.configuration.ConfigManager;
import me.blurmit.basics.listeners.*;
import me.blurmit.basics.punishments.PunishmentManager;
import me.blurmit.basics.rank.RankManager;
import me.blurmit.basics.scoreboard.ScoreboardManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class Basics extends JavaPlugin {

    @Getter
    private CommandManager commandManager;
    @Getter
    private ScoreboardManager scoreboardManager;
    @Getter
    private ConfigManager configManager;
    @Getter
    private RankManager rankManager;
    @Getter
    private PunishmentManager punishmentManager;

    @Override
    public void onEnable() {
        getLogger().info("Loading configurations...");
        this.configManager = new ConfigManager(this);

        if (configManager.getConfig().getBoolean("Ranks.Enabled")) {
            getLogger().info("Loading ranks...");
            this.rankManager = new RankManager(this);
            rankManager.createDefaultRank();
        }

        getLogger().info("Loading punishments...");
        this.punishmentManager = new PunishmentManager(this);

        getLogger().info("Registering commands...");
        this.commandManager = new CommandManager(this);
        getCommandManager().registerCommands();

        getLogger().info("Registering placeholders...");
        new CommandPlaceholder(this);
        new PlayerPlaceholder(this);
        new RankPlaceholder(this);
        ServerPlaceholder serverPlaceholder = new ServerPlaceholder(this);

        Plugin papi = getServer().getPluginManager().getPlugin("PlaceholderAPI");
        if (papi != null && papi.isEnabled()) {
            new PapiPlaceholder(this);
            serverPlaceholder.register();
        }

        getLogger().info("Registering scoreboard...");
        scoreboardManager = new ScoreboardManager(this);

        getLogger().info("Registering listeners...");
        new AsyncChatListener(this);
        new PlayerConnectionListener(this);


        getLogger().info(getName() + " has been enabled!");
    }

    @Override
    public void onDisable() {
        if (getRankManager() != null && getRankManager().getStorage().getDatabaseManager() != null) {
            getRankManager().getStorage().getDatabaseManager().shutdown();
        }

        if (getPunishmentManager() != null && getPunishmentManager().getStorage().getDatabaseManager() != null) {
            getPunishmentManager().getStorage().getDatabaseManager().shutdown();
        }

        getLogger().info(getName() + " has been disabled!");
    }

}
