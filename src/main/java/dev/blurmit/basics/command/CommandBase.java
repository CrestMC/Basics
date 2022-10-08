package dev.blurmit.basics.command;

import dev.blurmit.basics.Basics;
import org.bukkit.command.defaults.BukkitCommand;
import org.jetbrains.annotations.NotNull;

public abstract class CommandBase extends BukkitCommand {

    protected CommandBase(@NotNull String name) {
        super(name);
    }

    public void registerCommand() {
        Basics.getInstance().getCommandManager().register(getName(), this);
    }

}
