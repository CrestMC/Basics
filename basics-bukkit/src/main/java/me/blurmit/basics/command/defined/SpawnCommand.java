package me.blurmit.basics.command.defined;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.lang.Messages;
import me.blurmit.basics.util.placeholder.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

public class SpawnCommand extends CommandBase {

    private final Basics plugin;

    public SpawnCommand(Basics plugin) {
        super(plugin.getName());
        setName("spawn");
        setDescription("Teleports a player to the spawn location");
        setUsage("/spawn");
        setPermission("basics.commands.spawn");

        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.NO_PERMISSION + "", sender, this, args));
            return true;
        }

        Location spawnLocation;

        try {
            String[] locationString = plugin.getConfigManager().getConfig().getString("Spawn-Location").split(", ");
            spawnLocation = new Location(
                    Bukkit.getWorld(locationString[0]),
                    Double.parseDouble(locationString[1]),
                    Double.parseDouble(locationString[2]),
                    Double.parseDouble(locationString[3]),
                    Float.parseFloat(locationString[4]),
                    Float.parseFloat(locationString[5])
            );
        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException | NullPointerException e) {
            spawnLocation = plugin.getServer().getWorlds().get(0).getSpawnLocation();
        }

        if (!(sender instanceof Player)) {
            if (args.length != 1) {
                sender.sendMessage(Placeholders.parsePlaceholder(Messages.INVALID_ARGS + "", sender, this, args));
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                sender.sendMessage(Placeholders.parsePlaceholder(Messages.PLAYER_NOT_FOUND + "", args[0]));
                return true;
            }

            target.teleport(spawnLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.TELEPORTED_SPAWN + "", target, this, args));
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.teleport(spawnLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
            player.sendMessage(Placeholders.parsePlaceholder(Messages.TELEPORTED_SPAWN + "", player, this, args));
            return true;
        }

        if (!player.hasPermission("basics.spawn.other")) {
            player.sendMessage(Placeholders.parsePlaceholder(Messages.NO_PERMISSION_SUBCOMMAND + "", sender, this, args));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(Placeholders.parsePlaceholder(Messages.PLAYER_NOT_FOUND + "", args[0]));
            return true;
        }

        target.teleport(spawnLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
        player.sendMessage(Placeholders.parsePlaceholder(Messages.TELEPORTED_SPAWN + "", target, this, args));

        return true;
    }

}
