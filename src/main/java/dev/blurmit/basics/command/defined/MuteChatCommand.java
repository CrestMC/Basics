package dev.blurmit.basics.command.defined;

import dev.blurmit.basics.Basics;
import dev.blurmit.basics.command.CommandBase;
import dev.blurmit.basics.util.Booleans;
import dev.blurmit.basics.util.Placeholders;
import dev.blurmit.basics.util.lang.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class MuteChatCommand extends CommandBase implements Listener {

    private final Basics plugin;

    public MuteChatCommand(Basics plugin) {
        super(plugin.getName());
        setName("mutechat");
        setDescription("Toggles the chat for players without the bypass permission");
        setAliases(Arrays.asList("togglechat", "disablechat"));
        setUsage("Usage: /mutechat");
        setPermission("basics.commands.mutechat");

        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean execute(CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.NO_PERMISSION + "", sender, this, args));
            return true;
        }

        boolean isToggled = plugin.getConfigManager().getConfig().getBoolean("Chat-Enabled");

        plugin.getConfigManager().getConfig().set("Chat-Enabled", !isToggled);
        plugin.getConfigManager().saveConfig();

        Bukkit.broadcast(Placeholders.parsePlaceholder(Messages.MUTE_CHAT_TOGGLE + "", sender, false, Booleans.getFancyBoolean(!isToggled)), "basics.mutechat.bypass");

        return true;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (!event.isCancelled()) {
            if (plugin.getConfigManager().getConfig().getBoolean("Chat-Enabled")) {
                return;
            }

            if (event.getPlayer().hasPermission("basics.mutechat.bypass")) {
                return;
            }

            event.getPlayer().sendMessage(Placeholders.parsePlaceholder(Messages.MUTE_CHAT_MESSAGE + ""));
            event.setCancelled(true);
        }
    }

}
