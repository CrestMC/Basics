package me.blurmit.basics.command.defined;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.placeholder.Placeholders;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class ClearChatCommand extends CommandBase {

    private final Basics plugin;

    public ClearChatCommand(Basics plugin) {
        super(plugin.getName());
        setName("clearchat");
        setDescription("Echo a message back to the sender");
        setAliases(Collections.singletonList("chatclear"));
        setUsage("/clearchat");
        setPermission("basics.commands.clearchat");

        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.NO_PERMISSION + "", sender, this, args));
            return true;
        }

        plugin.getServer().getOnlinePlayers().forEach(onlinePlayer -> {
            if (!onlinePlayer.hasPermission("basics.clearchat.bypass")) {
                for (int i = 0; i < 500; i++) {
                    onlinePlayer.sendMessage(ChatColor.RESET + "");
                }
            }
        });
        Bukkit.broadcast(Placeholders.parsePlaceholder(Messages.CHAT_CLEARED + "", sender, this, args), "basics.clearchat.bypass");

        return true;
    }

}