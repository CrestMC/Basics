package me.blurmit.basics.command.defined.slowmode;

import me.blurmit.basics.Basics;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SlowmodeListener implements Listener {

    private final Basics plugin;
    private final SlowmodeCommand command;

    private final Map<UUID, Long> cooldownStorage;
    public long cooldown;

    public SlowmodeListener(Basics plugin, SlowmodeCommand command) {
        this.plugin = plugin;
        this.command = command;

        this.cooldownStorage = new HashMap<>();
        this.cooldown = plugin.getConfigManager().getConfig().getLong("Slowmode-Delay");
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.getPlayer().hasPermission("basics.slowmode.bypass")) {
            return;
        }

        if (cooldownStorage.containsKey(event.getPlayer().getUniqueId())) {

            long secondsLeft = ((cooldownStorage.get(event.getPlayer().getUniqueId()) / 1000) + cooldown) - (System.currentTimeMillis() / 1000);

            if (secondsLeft > 0) {
                event.getPlayer().sendMessage(Placeholders.parsePlaceholder(Messages.SLOWMODE_MESSAGE + "", event.getPlayer(), command, null, null, event.isAsynchronous(), secondsLeft + ""));
                event.setCancelled(true);
                return;
            }

            cooldownStorage.remove(event.getPlayer().getUniqueId());
        }

        cooldownStorage.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

}
