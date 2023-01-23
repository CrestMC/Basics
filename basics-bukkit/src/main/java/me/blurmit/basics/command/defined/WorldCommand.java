package me.blurmit.basics.command.defined;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collections;

public class WorldCommand extends CommandBase {

    private final Basics plugin;

    public WorldCommand(Basics plugin) {
        super(plugin.getName());
        setName("world");
        setDescription("Teleports a player to another world");
        setAliases(Collections.singletonList("worldtp"));
        setUsage("/world <world name>");
        setPermission("basics.command.world");

        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.NO_PERMISSION + "", sender, this, args));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.ONLY_PLAYERS + "", sender, this, args));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.INVALID_ARGS + "", sender, this, args));
            return true;
        }

        File worldFile = new File("./" + args[0].toLowerCase());
        File dataFolder = new File("./" + args[0].toLowerCase() + "/data");
        File uidFile = new File("./" + args[0].toLowerCase() + "/uid.dat");

        Player player = (Player) sender;
        Location location;

        if (worldFile.exists() && worldFile.isDirectory() && dataFolder.exists() && dataFolder.isDirectory() && uidFile.exists()) {
            location = Bukkit.createWorld(new WorldCreator(args[0])).getSpawnLocation();
        } else {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.INVALID_WORLD + "", sender, this, args));
            return true;
        }

        player.teleport(location);
        sender.sendMessage(Placeholders.parsePlaceholder(Messages.WORLD_TELEPORTED + "", sender, this, args));
        return true;
    }

}
