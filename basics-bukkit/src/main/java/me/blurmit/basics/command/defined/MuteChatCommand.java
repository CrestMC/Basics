package me.blurmit.basics.command.defined;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.Booleans;
import me.blurmit.basics.util.placeholder.Placeholders;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class MuteChatCommand extends CommandBase implements Listener {

    private final Basics plugin;
    private boolean isMuted;

    public MuteChatCommand(Basics plugin) {
        super(plugin.getName());
        setName("mutechat");
        setDescription("Toggles the chat for players without the bypass permission");
        setAliases(Arrays.asList("togglechat", "disablechat"));
        setUsage("/mutechat");
        setPermission("basics.commands.mutechat");

        this.plugin = plugin;
        this.isMuted = plugin.getConfigManager().getConfig().getBoolean("Chat-Enabled");

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean execute(CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.NO_PERMISSION + "", sender, this, args));
            return true;
        }

        isMuted = !isMuted;
        plugin.getConfigManager().getConfig().set("Chat-Enabled", isMuted);
        plugin.getConfigManager().saveConfig();

        Bukkit.broadcastMessage(Placeholders.parsePlaceholder(Messages.MUTE_CHAT_TOGGLE + "", sender, false, Booleans.getFancyBoolean(isMuted)));

        return true;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!isMuted) {
            return;
        }

        if (event.getPlayer().hasPermission("basics.mutechat.bypass")) {
            return;
        }

        event.getPlayer().sendMessage(Placeholders.parsePlaceholder(Messages.MUTE_CHAT_MESSAGE + ""));
        event.setCancelled(true);
    }

}
