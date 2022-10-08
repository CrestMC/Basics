package dev.blurmit.basics;

import dev.blurmit.basics.command.CommandManager;
import dev.blurmit.basics.configuration.ConfigManager;
import dev.blurmit.basics.listener.ChatMessageListener;
import dev.blurmit.basics.listener.ConnectionMessageListener;
import dev.blurmit.basics.placeholder.CommandPlaceholder;
import dev.blurmit.basics.placeholder.PlayerPlaceholder;
import dev.blurmit.basics.placeholder.RankPlaceholder;
import dev.blurmit.basics.placeholder.ServerPlaceholder;
import dev.blurmit.basics.scoreboard.ScoreboardManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class Basics extends JavaPlugin {

    @Getter
    private static Basics instance;
    @Getter
    private CommandManager commandManager;
    @Getter
    private ScoreboardManager scoreboardManager;
    @Getter
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        instance = this;

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
        new ChatMessageListener(this);
        new ConnectionMessageListener(this);

        getLogger().info(getName() + " has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info(getName() + " has been disabled!");
    }

}
