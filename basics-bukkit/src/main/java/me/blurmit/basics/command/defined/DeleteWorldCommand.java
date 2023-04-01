package me.blurmit.basics.command.defined;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collections;

public class DeleteWorldCommand extends CommandBase {

    private final Basics plugin;

    public DeleteWorldCommand(Basics plugin) {
        super(plugin.getName());
        setName("deleteworld");
        setDescription("Deletes a world");
        setAliases(Collections.singletonList("worlddelete"));
        setUsage("/deleteworld <world name>");
        setPermission("basics.command.deleteworld");

        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Placeholders.parse(Messages.NO_PERMISSION + "", sender, this, args));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(Placeholders.parse(Messages.INVALID_ARGS + "", sender, this, args));
            return true;
        }


        String worldName = args[0].toLowerCase();
        String worldContainer = plugin.getServer().getWorldContainer().getPath();

        File worldFile = new File(worldContainer + "/" + worldName);
        File dataFile = new File(worldContainer + "/" + worldName + "/data");
        File regionFile = new File(worldContainer + "/" + worldName + "/region");

        if (!worldFile.exists() || !worldFile.isDirectory() || !dataFile.exists() || !regionFile.isDirectory()) {
            sender.sendMessage(Placeholders.parse(Messages.INVALID_WORLD + "", sender, false, worldName));
            return true;
        }

        try {
            plugin.getServer().getWorld(worldName).getPlayers().forEach(player -> player.teleport(plugin.getServer().getWorlds().get(0).getSpawnLocation()));
        } catch (NullPointerException ignored) {}

        if (plugin.getServer().getWorld(worldName) != null) {
            plugin.getServer().unloadWorld(worldName, false);
        }

        for (File file : worldFile.listFiles()) {
            file.delete();
        }
        worldFile.delete();

        sender.sendMessage(Placeholders.parse(Messages.WORLD_DELETED + "", sender, this, args));
        return true;
    }

}
