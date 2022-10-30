package me.blurmit.basics.placeholder;

import me.blurmit.basics.Basics;
import me.blurmit.basics.events.PlaceholderRequestEvent;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerPlaceholder implements Listener {

    public PlayerPlaceholder(Basics plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlaceholderRequest(PlaceholderRequestEvent event) {
        String placeholder = event.getPlaceholder().toLowerCase();

        if (placeholder.equals("player-name")) {
            try {
                event.setResponse(event.getPlayer().getName());
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.equals("player-display-name")) {
            try {
                event.setResponse(event.getPlayer().getDisplayName());
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.equals("player-location")) {
            try {
                Location loc = event.getPlayer().getLocation();
                event.setResponse(Math.round(loc.getX()) + ", " + Math.round(loc.getY()) + ", " + Math.round(loc.getZ()) + ", " + loc.getWorld().getName());
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.equals("player-uuid")) {
            try {
                event.setResponse(event.getPlayer().getUniqueId().toString());
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.equals("player-ping")) {
            // Legacy servers do not have Player#getPing
            // To counter this we have to use reflection to get it directly from the craftplayer handle
            try {
                Object craftPlayer = event.getPlayer().getClass().getMethod("getHandle").invoke(event.getPlayer());
                int ping = (int) craftPlayer.getClass().getField("ping").get(craftPlayer);
                event.setResponse(ping + "");
            } catch (ReflectiveOperationException e) {
                event.setResponse(event.getPlayer().getPing() + "");
            } catch (Exception e) {
                event.setResponse("30");
            }
        }

        if (placeholder.equals("player-ip")) {
            try {
                event.setResponse(event.getPlayer().getAddress().getHostString());
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.equals("player-world")) {
            try {
                event.setResponse(event.getPlayer().getWorld().getName());
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.equals("player-health")) {
            try {
                event.setResponse(String.valueOf(event.getPlayer().getHealth()));
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.equals("player-health-max")) {
            try {
                event.setResponse(String.valueOf(event.getPlayer().getMaxHealth()));
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.equals("player-health-percent")) {
            try {
                event.setResponse(String.valueOf(event.getPlayer().getHealth() / event.getPlayer().getMaxHealth() * 100));
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.equals("player-level")) {
            try {
                event.setResponse(String.valueOf(event.getPlayer().getLevel()));
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.equals("player-exp")) {
            try {
                event.setResponse(String.valueOf(event.getPlayer().getExp()));
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.equals("player-exp-max")) {
            try {
                event.setResponse(String.valueOf(event.getPlayer().getExpToLevel()));
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.equals("player-exp-percent")) {
            try {
                event.setResponse(String.valueOf(event.getPlayer().getExp() / event.getPlayer().getExpToLevel() * 100));
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.equals("player-exp-level")) {
            try {
                event.setResponse(String.valueOf(event.getPlayer().getLevel()));
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.equals("player-exp-level-percent")) {
            try {
                event.setResponse(String.valueOf(event.getPlayer().getLevel() / event.getPlayer().getExpToLevel() * 100));
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.equals("player-exp-level-next")) {
            try {
                event.setResponse(String.valueOf(event.getPlayer().getLevel() + 1));
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.equals("player-item-in-hand")) {
            try {
                event.setResponse(Messages.getFancyName(event.getPlayer().getInventory().getItemInMainHand().getType().name()));
            } catch (Exception e) {
                event.setResponse("");
            }
        }

    }

}
