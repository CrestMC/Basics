package me.blurmit.basics.command;

import me.blurmit.basics.Basics;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class CommandBase extends BukkitCommand implements TabCompleter {

    private long cooldown;
    private TabCompleter tabCompleter;

    protected CommandBase(@NotNull String name) {
        super(name);

        this.cooldown = 0;
        this.tabCompleter = null;
    }

    public void registerCommand() {
        JavaPlugin.getPlugin(Basics.class).getCommandManager().register(getName(), this);

        if (tabCompleter != null) {
            PluginCommand command = Bukkit.getPluginCommand(getName());

            if (command != null) {
                command.setTabCompleter(tabCompleter);
            }
        }
    }

    public void setTabCompleter(TabCompleter tabCompleter) {
        this.tabCompleter = tabCompleter;
    }

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
    }

    public long getCooldown() {
        return cooldown;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }

}
