package me.blurmit.basics.command.defined;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class HelpCommand extends CommandBase {

    private final Basics plugin;

    public HelpCommand(Basics plugin) {
        super(plugin.getName());
        setName("help");
        setDescription("Get server help.");
        setUsage("/help");
        setPermission("basics.command.help");

        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        sender.sendMessage(Placeholders.parse(Messages.HELP_CMD + ""));
        return true;
    }
}
