package me.blurmit.basics.command.defined;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.Booleans;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;

public class CreateWorldCommand extends CommandBase {

    private final Basics plugin;

    private String worldName;
    private WorldType worldType;
    private long seed;
    private boolean generateStructures;
    private World.Environment environment;

    public CreateWorldCommand(Basics plugin) {
        super(plugin.getName());
        setName("createworld");
        setDescription("Creates a world");
        setAliases(Arrays.asList("worldcreate", "makeworld"));
        setUsage("/createworld <name> [type] [gen structures] [seed]");
        setPermission("basics.command.createworld");

        this.worldName = "newWorld";
        this.worldType = WorldType.NORMAL;
        this.environment = World.Environment.NORMAL;
        this.seed = 0;
        this.generateStructures = true;
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.NO_PERMISSION + "", sender, this, args));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.INVALID_ARGS + "", sender, this, args));
            return true;
        }

        worldName = args[0];

        if (args.length >= 2) {
            switch (args[1].toLowerCase()) {
                case "flat":
                    worldType = WorldType.FLAT;
                    break;
                case "normal":
                    worldType = WorldType.NORMAL;
                    break;
                case "amplified":
                    worldType = WorldType.AMPLIFIED;
                    break;
                default:
                    sender.sendMessage(Placeholders.parsePlaceholder(Messages.INVALID_WORLD_TYPE + "", sender, this, args));
                    return true;
            }
        }

        if (args.length >= 3) {
            generateStructures = Booleans.isFancyBoolean(args[2]);
        }

        if (args.length >= 4) {
            try {
                seed = Long.parseLong(args[3]);
            } catch (IllegalArgumentException e) {
                sender.sendMessage(Placeholders.parsePlaceholder(Messages.NUMBER_INVALID + "", args[3]));
                return true;
            }
        }

        File worldFile = new File("./" + args[0]);
        File dataFolder = new File("./" + args[0] + "/data");
        File uidFile = new File("./" + args[0] + "/uid.dat");

        if (worldFile.exists() && worldFile.isDirectory() && dataFolder.exists() && dataFolder.isDirectory() && uidFile.exists()) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.WORLD_ALREADY_EXISTS + "", sender, this, args));
            return true;
        }

        Bukkit.createWorld(new WorldCreator(worldName.toLowerCase())
                .type(worldType)
                .generateStructures(generateStructures)
                .seed(seed));

        sender.sendMessage(Placeholders.parsePlaceholder(Messages.WORLD_CREATED + "", sender, this, args));

        return true;
    }
}
