package me.blurmit.basicsbungee.configuration;

import com.google.common.io.ByteStreams;
import lombok.Getter;
import me.blurmit.basicsbungee.BasicsBungee;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.logging.Level;

public class ConfigManager {

    private final BasicsBungee plugin;

    @Getter
    public Configuration config;

    @Getter
    public Configuration languageConfig;

    private final File configFile;
    private final File languageFile;

    public ConfigManager(BasicsBungee plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        this.languageFile = new File(plugin.getDataFolder(), "language.yml");

        loadConfigurations();
    }

    public void loadConfigurations() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        try {
            InputStream inputStream;
            OutputStream outputStream;

            if (!configFile.exists()) {
                inputStream = getClass().getClassLoader().getResourceAsStream("config.yml");
                outputStream = Files.newOutputStream(configFile.toPath());
                ByteStreams.copy(inputStream, outputStream);
            }

            if (!languageFile.exists()) {
                inputStream = getClass().getClassLoader().getResourceAsStream("language.yml");
                outputStream = Files.newOutputStream(languageFile.toPath());
                ByteStreams.copy(inputStream, outputStream);
            }

            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
            languageConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(languageFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred whilst loading the configuration files", e);
        }
    }

    public void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred whilst saving the configuration file", e);
        }
    }

    public void saveLanguageConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(languageConfig, languageFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred whilst saving the language file", e);
        }
    }

    public void reloadConfig() {
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred whilst reload the configuration file", e);
        }
    }

    public void reloadLanguageConfig() {
        try {
            languageConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(languageFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred whilst reload the language file", e);
        }
    }

}
