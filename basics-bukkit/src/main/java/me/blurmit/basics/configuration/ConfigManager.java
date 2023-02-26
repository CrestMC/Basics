package me.blurmit.basics.configuration;

import lombok.Getter;
import me.blurmit.basics.Basics;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class ConfigManager {

    private final Basics plugin;

    @Getter
    public FileConfiguration config;

    @Getter
    public FileConfiguration languageConfig;

    @Getter
    public FileConfiguration scoreboardConfig;

    @Getter
    public FileConfiguration ranksConfig;

    private final File configFile;
    private final File languageFile;
    private final File scoreboardFile;
    private final File ranksFile;

    public ConfigManager(Basics plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        this.languageFile = new File(plugin.getDataFolder(), "language.yml");
        this.scoreboardFile = new File(plugin.getDataFolder(), "scoreboard.yml");
        this.ranksFile = new File(plugin.getDataFolder(), "ranks.yml");

        loadConfigurations();
    }

    public void loadConfigurations() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }

        if (!languageFile.exists()) {
            plugin.saveResource("language.yml", false);
        }

        if (!scoreboardFile.exists()) {
            plugin.saveResource("scoreboard.yml", false);
        }

        if (!ranksFile.exists()) {
            plugin.saveResource("ranks.yml", false);
        }

        plugin.getLogger().info("Loading config.yml...");
        config = YamlConfiguration.loadConfiguration(configFile);

        plugin.getLogger().info("Loading language.yml...");
        languageConfig = YamlConfiguration.loadConfiguration(languageFile);

        plugin.getLogger().info("Loading scoreboard.yml...");
        scoreboardConfig = YamlConfiguration.loadConfiguration(scoreboardFile);

        plugin.getLogger().info("Loading ranks.yml...");
        ranksConfig = YamlConfiguration.loadConfiguration(ranksFile);
    }

    public void saveConfig() {
        try {
            getConfig().save(configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred whilst saving the configuration file", e);
        }
    }

    public void saveRanksConfig() {
        try {
            getRanksConfig().save(ranksFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred whilst saving the ranks configuration file", e);
        }
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void reloadLanguageConfig() {
        languageConfig = YamlConfiguration.loadConfiguration(languageFile);
    }

    public void reloadScoreboardConfig() {
        scoreboardConfig = YamlConfiguration.loadConfiguration(scoreboardFile);
    }

    public void reloadRanksConfig() {
        ranksConfig = YamlConfiguration.loadConfiguration(ranksFile);
    }

}
