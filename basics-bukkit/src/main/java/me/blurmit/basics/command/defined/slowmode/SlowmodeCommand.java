package me.blurmit.basics.command.defined.slowmode;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class SlowmodeCommand extends CommandBase {

    private final Basics plugin;
    private final SlowmodeListener listener;

    public SlowmodeCommand(Basics plugin) {
        super(plugin.getName());
        setName("slowmode");
        setDescription("Sets the chat cooldown to a specified amount of seconds");
        setUsage("/slowmode <time>");
        setAliases(Arrays.asList("chatcooldown", "slowchat"));
        setPermission("basics.command.slowmode");

        this.plugin = plugin;
        this.listener = new SlowmodeListener(plugin, this);

        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    @Override
    public boolean execute(CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Placeholders.parse(Messages.NO_PERMISSION + "", sender, this, args));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(Placeholders.parse(Messages.INVALID_ARGS + "", sender, this, args));
            return true;
        }

        long delay;

        try {
            delay = Long.parseLong(args[0]);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(Placeholders.parse(Messages.NUMBER_INVALID + "", args[0]));
            return true;
        }

        listener.cooldown = delay;
        plugin.getConfigManager().getConfig().set("Slowmode-Delay", delay);
        plugin.getConfigManager().saveConfig();

        sender.sendMessage(Placeholders.parse(Messages.SLOWMODE_SET + "", sender, this, args));

        return true;
    }

}
