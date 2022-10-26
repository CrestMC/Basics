package me.blurmit.basics.command;

import me.blurmit.basics.Basics;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public abstract class CommandBase extends BukkitCommand {

    protected CommandBase(@NotNull String name) {
        super(name);
    }

    public void registerCommand() {
        JavaPlugin.getPlugin(Basics.class).getCommandManager().register(getName(), this);
    }

}
