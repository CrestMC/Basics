package me.blurmit.basics.command.defined;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class MobKillCommand extends CommandBase {

    private final Basics plugin;

    public MobKillCommand(Basics plugin) {
        super(plugin.getName());
        setName("mobkill");
        setDescription("Kills all mobs of a certain type");
        setUsage("/mobkill <type> <radius> [world]");
        setPermission("basics.command.mobkill");
        setAliases(Arrays.asList("killmobs", "removeentities", "deleteentities", "killentity", "deleteentity", "removeentity", "killentities"));

        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Placeholders.parse(Messages.NO_PERMISSION + ""));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(Placeholders.parse(Messages.INVALID_ARGS + "", sender, this, args));
            return true;
        }

        EntityType type = EntityType.fromName(args[0]);
        if (!args[0].equals("all")) {
            if (type == null) {
                sender.sendMessage(Placeholders.parse(Messages.INVALID_ENTITY_TYPE + "", sender, this, args));
                return true;
            }
        }

        World world = plugin.getServer().getWorlds().get(0);
        Location location = world.getSpawnLocation();
        long radius = -1;

        if (args.length > 1) {
            try { radius = Long.parseLong(args[1]); } catch (NumberFormatException ignored) {}
        }

        if (args.length > 2) {
            world = plugin.getServer().getWorld(args[2]);

            if (world == null) {
                sender.sendMessage(Placeholders.parse(Messages.INVALID_WORLD + "", sender, false, args[2]));
                return true;
            }
        }

        if (sender instanceof Player) {
            location = ((Player) sender).getLocation();
        }

        AtomicLong amount = new AtomicLong();
        if (radius == -1) {
            Stream<Entity> entityStream = world.getEntities().stream()
                    .filter(entity -> !(entity instanceof Player))
                    .filter(entity -> type == null || entity.getType() == type)
                    .filter(entity -> entity.getType() != EntityType.ARMOR_STAND);
            entityStream.forEach(entity -> {
                entity.remove();
                amount.getAndIncrement();
            });
        } else {
            Stream<Entity> entityStream = world.getNearbyEntities(location, radius, radius, radius).stream()
                    .filter(entity -> !(entity instanceof Player))
                    .filter(entity -> type == null || entity.getType() == type)
                    .filter(entity -> entity.getType() != EntityType.ARMOR_STAND);
            entityStream.forEach(entity -> {
                entity.remove();
                amount.getAndIncrement();
            });
        }

        sender.sendMessage(Placeholders.parse(Messages.MOBS_REMOVED + "", sender, false, amount.get() + "", world.getName()));
        return true;
    }

}
