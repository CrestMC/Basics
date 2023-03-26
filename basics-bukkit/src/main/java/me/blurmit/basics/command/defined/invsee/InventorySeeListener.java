package me.blurmit.basics.command.defined.invsee;

import me.blurmit.basics.Basics;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class InventorySeeListener implements Listener {

    private final Basics plugin;
    private final Set<UUID> inventoryViewers;

    public InventorySeeListener(Basics plugin) {
        this.plugin = plugin;
        this.inventoryViewers = new HashSet<>();
    }

    @EventHandler
    public void onInventoryInteract(InventoryInteractEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (!inventoryViewers.contains(player.getUniqueId())) {
            return;
        }

        if (player.hasPermission("basics.command.invsee.interact")) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        inventoryViewers.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        inventoryViewers.remove(event.getPlayer().getUniqueId());
    }

    public Set<UUID> getInventoryViewers() {
        return inventoryViewers;
    }

}
