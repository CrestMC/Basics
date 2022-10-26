package me.blurmit.basics.placeholder;

import me.blurmit.basics.Basics;
import me.blurmit.basics.events.PlaceholderRequestEvent;
import me.blurmit.basics.util.Booleans;
import me.blurmit.basics.util.Gamemodes;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Arrays;

public class CommandPlaceholder implements Listener {

    public CommandPlaceholder(Basics plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlaceholderRequest(PlaceholderRequestEvent event) {
        String placeholder = event.getPlaceholder().toLowerCase();

        if (placeholder.startsWith("argument-")) {
            try {
                int arg = Integer.parseInt(placeholder.replace("argument-", ""));
                event.setResponse(event.getArguments()[arg]);
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.startsWith("arguments-")) {
            try {
                String response = String.join(" ",
                        Arrays.copyOfRange(event.getArguments(),
                                Integer.parseInt(placeholder.replace("arguments-",
                                        "")), event.getArguments().length));
                event.setResponse(response);
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.startsWith("gamemode-from-argument-")) {
            try {
                int arg = Integer.parseInt(placeholder.replace("gamemode-from-argument-", ""));
                event.setResponse(Gamemodes.getGamemode(event.getArguments()[arg]).name().toLowerCase());
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.equals("fly-boolean")) {
            try {
                if (event.getArguments().length == 0) {
                    event.setResponse(String.valueOf(event.getPlayer().getAllowFlight()));
                    return;
                }
                event.setResponse(String.valueOf(Booleans.isFancyBoolean(event.getArguments()[0])));
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.equals("sender-name")) {
            try {
                event.setResponse(event.getSender().getName());
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.equals("command-usage")) {
            try {
                event.setResponse(event.getCommand().getUsage());
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.equals("command-name")) {
            try {
                event.setResponse(event.getCommand().getName());
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.equals("command-label")) {
            try {
                event.setResponse(event.getCommand().getLabel());
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.equals("command-aliases")) {
            try {
                event.setResponse(String.join(", ", event.getCommand().getAliases()));
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.equals("command-permission")) {
            try {
                event.setResponse(event.getCommand().getPermission());
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.equals("command-permission-message")) {
            try {
                event.setResponse(event.getCommand().getPermissionMessage());
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.equals("command-description")) {
            try {
                event.setResponse(event.getCommand().getDescription());
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.equals("subcommand-name")) {
            try {
                event.setResponse(event.getSubCommand().getName());
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.equals("subcommand-usage")) {
            try {
                event.setResponse(event.getSubCommand().getUsage());
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.equals("subcommand-permission")) {
            try {
                event.setResponse(event.getSubCommand().getPermission());
            } catch (Exception e) {
                event.setResponse("");
            }
        }

        if (placeholder.startsWith("replacement-")) {
            try {
                int index = Integer.parseInt(placeholder.replace("replacement-", ""));
                event.setResponse(Arrays.asList(event.getReplacements()).get(index).toString());
            } catch (Exception e) {
                event.setResponse("");
            }
        }

    }

}
