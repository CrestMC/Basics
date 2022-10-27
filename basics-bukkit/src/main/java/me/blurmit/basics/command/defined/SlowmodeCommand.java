package me.blurmit.basics.command.defined;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.placeholder.Placeholders;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SlowmodeCommand extends CommandBase implements Listener {

    private final Basics plugin;
    private final Map<UUID, Long> cooldownStorage;
    public long cooldown;

    public SlowmodeCommand(Basics plugin) {
        super(plugin.getName());
        setName("slowmode");
        setDescription("Sets the chat cooldown to a specified amount of seconds");
        setUsage("/slowmode <time>");
        setAliases(Arrays.asList("chatcooldown", "slowchat"));
        setPermission("basics.commands.slowmode");

        this.plugin = plugin;
        this.cooldown = plugin.getConfigManager().getConfig().getLong("Slowmode-Delay");
        this.cooldownStorage = new HashMap<>();

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean execute(CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.NO_PERMISSION + "", sender, this, args));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.INVALID_ARGS + "", sender, this, args));
            return true;
        }

        long delay;

        try {
            delay = Long.parseLong(args[0]);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.NUMBER_INVALID + "", args[0]));
            return true;
        }

        cooldown = delay;
        plugin.getConfigManager().getConfig().set("Slowmode-Delay", delay);
        plugin.getConfigManager().saveConfig();

        sender.sendMessage(Placeholders.parsePlaceholder(Messages.SLOWMODE_SET + "", sender, this, args));

        return true;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent event) {
        if (!event.isCancelled()) {
            if (event.getPlayer().hasPermission("basics.slowmode.bypass")) {
                return;
            }

            if (cooldownStorage.containsKey(event.getPlayer().getUniqueId())) {

                long secondsLeft = ((cooldownStorage.get(event.getPlayer().getUniqueId()) / 1000) + cooldown) - (System.currentTimeMillis() / 1000);

                if (secondsLeft > 0) {
                    event.getPlayer().sendMessage(Placeholders.parsePlaceholder(Messages.SLOWMODE_MESSAGE + "", event.getPlayer(), event.isAsynchronous(), secondsLeft));
                    event.setCancelled(true);
                    return;
                }

            }

            cooldownStorage.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
        }
    }

}
