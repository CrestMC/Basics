package dev.blurmit.basics.configuration;

import com.google.common.io.ByteStreams;
import dev.blurmit.basics.Basics;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;

public class ConfigManager {

    private final Basics plugin;

    private File defaultConfigFile;
    private File messagesFile;
    private File scoreboardFile;

    private YamlConfiguration defaultConfig;
    private YamlConfiguration scoreboardConfig;
    private YamlConfiguration messagesConfig;

    public ConfigManager(Basics plugin) {
        this.plugin = plugin;
        registerConfigs();
    }

    private void registerConfigs() {
        this.defaultConfigFile = new File(plugin.getDataFolder(), "config.yml");
        this.messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        this.scoreboardFile = new File(plugin.getDataFolder(), "scoreboard.yml");
        loadConfigs();
    }

    public void loadConfigs() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        if (!defaultConfigFile.exists()) {
            try {
                // Default config
                InputStream configInput = getClass().getClassLoader().getResourceAsStream("config.yml");
                OutputStream configOutput = new FileOutputStream(defaultConfigFile);
                ByteStreams.copy(configInput, configOutput);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        defaultConfig = YamlConfiguration.loadConfiguration(defaultConfigFile);

        if (!messagesFile.exists()) {
            try {
                // Messages config
                InputStream messagesInput = getClass().getClassLoader().getResourceAsStream("messages.yml");
                OutputStream messagesOutput = new FileOutputStream(messagesFile);
                ByteStreams.copy(messagesInput, messagesOutput);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        if (!scoreboardFile.exists()) {
            try {
                // Scoreboard config
                InputStream scoreboardInput = getClass().getClassLoader().getResourceAsStream("scoreboard.yml");
                OutputStream scoreboardOutput = new FileOutputStream(scoreboardFile);
                ByteStreams.copy(scoreboardInput, scoreboardOutput);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        scoreboardConfig = YamlConfiguration.loadConfiguration(scoreboardFile);
    }

    public YamlConfiguration getConfig() {
        return defaultConfig;
    }

    public YamlConfiguration getMessages() {
        return messagesConfig;
    }

    public YamlConfiguration getScoreboardConfig() {
        return scoreboardConfig;
    }

    public void saveConfig() {
        try {
            defaultConfig.save(defaultConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadConfig() {
        try {
            getConfig().save(defaultConfigFile);
        } catch (IOException ignored) {
        } finally {
            defaultConfig = YamlConfiguration.loadConfiguration(defaultConfigFile);
        }
    }

    public void saveMessages() {
        try {
            messagesConfig.save(messagesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadMessages() {
        try {
            getMessages().save(messagesFile);
        } catch (IOException ignored) {
        } finally {
            defaultConfig = YamlConfiguration.loadConfiguration(messagesFile);
        }
    }

    public void saveScoreboard() {
        try {
            scoreboardConfig.save(scoreboardFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadScoreboard() {
        try {
            getScoreboardConfig().save(scoreboardFile);
        } catch (IOException ignored) {
        } finally {
            defaultConfig = YamlConfiguration.loadConfiguration(scoreboardFile);
        }
    }

    public void reloadConfigs() {
        reloadConfig();
        reloadMessages();
        reloadScoreboard();
    }

}
