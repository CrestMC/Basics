package me.blurmit.basics.command.defined;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.placeholder.Placeholders;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.Bukkit;
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
        setUsage("Usage: /deleteworld <world name>");
        setPermission("basics.commands.deleteworld");

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

        File worldFile = new File("./" + args[0].toLowerCase());
        File dataFile = new File("./" + args[0].toLowerCase() + "/data");
        File regionFile = new File("./" + args[0].toLowerCase() + "/region");
        File poiFile = new File("./" + args[0].toLowerCase() + "/poi");

        if (!worldFile.exists() || !worldFile.isDirectory() || !dataFile.exists() || !regionFile.isDirectory() || !poiFile.exists()) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.INVALID_WORLD + "", sender, this, args));
            return true;
        }

        try {
            Bukkit.getWorld(args[0]).getPlayers().forEach(player -> {
                player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
            });
        } catch (NullPointerException ignored) {
        }

        if (Bukkit.getWorld(args[0]) != null) {
            Bukkit.unloadWorld(args[0], true);
        }

        for (File dataFiles : dataFile.listFiles()) {
            dataFiles.delete();
        }

        for (File regionFiles : regionFile.listFiles()) {
            regionFiles.delete();
        }

        for (File poiFiles : poiFile.listFiles()) {
            poiFiles.delete();
        }

        for (File worldFiles : worldFile.listFiles()) {
            worldFiles.delete();
        }
        worldFile.delete();

        sender.sendMessage(Placeholders.parsePlaceholder(Messages.WORLD_DELETED + "", sender, this, args));
        return true;
    }

}
