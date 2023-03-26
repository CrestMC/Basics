package me.blurmit.basics.command.defined;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class WorldCommand extends CommandBase {

    private final Basics plugin;

    public WorldCommand(Basics plugin) {
        super(plugin.getName());
        setName("world");
        setDescription("Teleports a player to another world");
        setAliases("worldtp", "tpworld");
        setUsage("/world <world name>");
        setPermission("basics.command.world");

        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Placeholders.parse(Messages.NO_PERMISSION + "", sender, this, args));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Placeholders.parse(Messages.ONLY_PLAYERS + "", sender, this, args));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(Placeholders.parse(Messages.INVALID_ARGS + "", sender, this, args));
            return true;
        }

        String worldContainer = plugin.getServer().getWorldContainer().getPath();
        File worldFile = new File(worldContainer + "/" + args[0].toLowerCase());
        File dataFolder = new File(worldContainer + "/" + args[0].toLowerCase() + "/data");
        File uidFile = new File(worldContainer + "/" + args[0].toLowerCase() + "/uid.dat");

        Player player = (Player) sender;
        Location location;
        if (worldFile.exists() && worldFile.isDirectory() && dataFolder.exists() && dataFolder.isDirectory() && uidFile.exists()) {
            location = plugin.getServer().createWorld(new WorldCreator(args[0])).getSpawnLocation();
        } else {
            sender.sendMessage(Placeholders.parse(Messages.INVALID_WORLD + "", sender, false, args[0]));
            return true;
        }

        player.teleport(location);
        sender.sendMessage(Placeholders.parse(Messages.WORLD_TELEPORTED + "", sender, this, args));
        return true;
    }

}
