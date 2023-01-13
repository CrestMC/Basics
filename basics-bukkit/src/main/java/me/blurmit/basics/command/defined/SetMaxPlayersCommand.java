package me.blurmit.basics.command.defined;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.Reflector;
import me.blurmit.basics.util.lang.Messages;
import me.blurmit.basics.util.placeholder.Placeholders;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;

public class SetMaxPlayersCommand extends CommandBase {

    private final Basics plugin;

    public SetMaxPlayersCommand(Basics plugin) {
        super(plugin.getName());
        setName("setmaxplayers");
        setDescription("Sets the player limit of the server");
        setUsage("/setmaxplayers <max>");
        setAliases(Arrays.asList("setplayerlimit", "maxplayers"));
        setPermission("basics.command.setmaxplayers");

        this.plugin = plugin;
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

        int max;

        try {
            max = Integer.parseInt(args[0]);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.NUMBER_INVALID + "", args[0]));
            return true;
        }

        // Reflectively set the player limit field in the DedicatedPlayerList class
        try {
            Object playerList = Reflector.getOBCClass("CraftServer").getDeclaredMethod("getHandle").invoke(plugin.getServer());
            Field maxPlayers = playerList.getClass().getSuperclass().getDeclaredField("maxPlayers");
            maxPlayers.setAccessible(true);
            maxPlayers.set(playerList, max);
        } catch (ReflectiveOperationException e) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred whilst attempting to set the player limit", e);
        }

        // Save the new player limit to the server.properties file so it persists after restart
        try {
            FileInputStream input = new FileInputStream("./server.properties");
            Properties properties = new Properties();
            properties.load(input);
            input.close();

            FileOutputStream output = new FileOutputStream("./server.properties");
            properties.setProperty("max-players", max + "");
            properties.store(output, null);
            output.close();
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred whilst attempting to set the player limit", e);
        }

        sender.sendMessage(Placeholders.parsePlaceholder(Messages.MAX_PLAYERS_SET + ""));
        return true;
    }

}
