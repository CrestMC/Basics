package me.blurmit.basics.command.defined;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.Booleans;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;

public class CreateWorldCommand extends CommandBase {

    private final Basics plugin;

    private String name;
    private WorldType type;
    private long seed;
    private boolean doGenerateStructures;
    private boolean isHardcore;
    private World.Environment environment;

    public CreateWorldCommand(Basics plugin) {
        super(plugin.getName());
        setName("createworld");
        setDescription("Creates a world");
        setAliases(Arrays.asList("worldcreate", "makeworld"));
        setUsage("/createworld <name> [type] [environment] [generate structures] [seed] [hardcore]");
        setPermission("basics.command.createworld");

        this.name = "New_World";
        this.type = WorldType.NORMAL;
        this.environment = World.Environment.NORMAL;
        this.seed = 0;
        this.doGenerateStructures = true;
        this.isHardcore = false;
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Placeholders.parse(Messages.NO_PERMISSION + "", sender, this, args));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(Placeholders.parse(Messages.INVALID_ARGS + "", sender, this, args));
            return true;
        }

        name = args[0].toLowerCase();

        if (args.length > 1) {
            type = WorldType.getByName(args[1].toLowerCase());

            if (type == null) {
                sender.sendMessage(Placeholders.parse(Messages.INVALID_WORLD_TYPE + "", args[1]));
                return true;
            }
        }

        if (args.length > 2) {
            switch (args[2].toLowerCase()) {
                case "overworld":
                case "normal":
                    environment = World.Environment.NORMAL;
                    break;
                case "nether":
                case "hell":
                case "the_nether":
                    environment = World.Environment.NETHER;
                    break;
                case "end":
                case "the_end":
                    environment = World.Environment.THE_END;
                    break;
                default:
                    sender.sendMessage(Placeholders.parse(Messages.INVALID_WORLD_ENVIRONMENT + "", args[2]));
                    return true;
            }
        }

        if (args.length > 3) {
            doGenerateStructures = Booleans.isFancyBoolean(args[3]);
        }

        if (args.length > 4) {
            try {
                seed = Long.parseLong(args[4]);
            } catch (NumberFormatException e) {
                sender.sendMessage(Placeholders.parse(Messages.NUMBER_INVALID + "", args[4]));
                return true;
            }
        }

        if (args.length > 5) {
            isHardcore = Booleans.isFancyBoolean(args[5]);
        }

        String worldContainer = plugin.getServer().getWorldContainer().getPath();
        File worldFile = new File(worldContainer + "/" + name);
        File dataFolder = new File(worldContainer + "/" + name + "/data");
        File uidFile = new File(worldContainer + "/" + name + "/uid.dat");

        if (worldFile.exists() && worldFile.isDirectory() && dataFolder.exists() && dataFolder.isDirectory() && uidFile.exists()) {
            sender.sendMessage(Placeholders.parse(Messages.WORLD_ALREADY_EXISTS + "", sender, this, args));
            return true;
        }

        plugin.getServer().createWorld(new WorldCreator(name)
                .type(type)
                .environment(environment)
                .generateStructures(doGenerateStructures)
                .hardcore(isHardcore)
                .seed(seed));
        sender.sendMessage(Placeholders.parse(Messages.WORLD_CREATED + "", sender, this, args));

        return true;
    }
}
