package me.blurmit.basics.command.defined;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.lang.Messages;
import me.blurmit.basics.util.placeholder.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class HelpopCommand extends CommandBase {

    private final Basics plugin;

    public HelpopCommand (Basics plugin){
        super(plugin.getName());
        setName("helpop");
        setAliases(Arrays.asList("helpme", "messagestaff"));
        setDescription("Send a message to all active staff members.");
        setPermission("basics.command.helpop");
        setUsage("/helpop <message>");

        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] args) {
        Player player = (Player) commandSender;
        String message = Placeholders.parsePlaceholder(String.join(" ", Arrays.copyOfRange(args, 0, args.length)), player, this, args);

        plugin.getServer().getOnlinePlayers().forEach(loopplayer -> {
            if (!loopplayer.hasPermission("basics.helpop")) {
                return;
            }

            // send message
        });
        return true;
    }
}
