package me.blurmit.basics;

import me.blurmit.basics.command.CommandManager;
import me.blurmit.basics.listeners.AsyncChatListener;
import me.blurmit.basics.listeners.PlayerConnectionListener;
import me.blurmit.basics.placeholder.CommandPlaceholder;
import me.blurmit.basics.placeholder.PlayerPlaceholder;
import me.blurmit.basics.placeholder.RankPlaceholder;
import me.blurmit.basics.placeholder.ServerPlaceholder;
import me.blurmit.basics.rank.RankManager;
import me.blurmit.basics.scoreboard.ScoreboardManager;
import lombok.Getter;
import me.blurmit.basics.configuration.ConfigManager;
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

    @Override
    public void onEnable() {
        getLogger().info("Loading configurations...");
        this.configManager = new ConfigManager(this);

        getLogger().info("Registering commands...");
        this.commandManager = new CommandManager(this);
        getCommandManager().registerCommands();

        getLogger().info("Registering placeholders...");
        new CommandPlaceholder(this);
        new PlayerPlaceholder(this);
        new RankPlaceholder(this);
        new ServerPlaceholder(this);

        getLogger().info("Registering scoreboard...");
        scoreboardManager = new ScoreboardManager(this);

        getLogger().info("Registering listeners...");
        new AsyncChatListener(this);
        new PlayerConnectionListener(this);

        getLogger().info("Loading ranks...");
        this.rankManager = new RankManager(this);

        getLogger().info(getName() + " has been enabled!");
    }

    @Override
    public void onDisable() {
        if (getRankManager().getStorage().getDatabaseManager() != null) {
            getRankManager().getStorage().getDatabaseManager().shutdown();
        }

        getLogger().info(getName() + " has been disabled!");
    }

}
