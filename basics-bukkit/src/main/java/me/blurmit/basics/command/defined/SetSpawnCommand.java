package me.blurmit.basics.command.defined;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetSpawnCommand extends CommandBase {

    private final Basics plugin;

    public SetSpawnCommand(Basics plugin) {
        super(plugin.getName());
        setName("setspawn");
        setDescription("Sets the spawnpoint for players");
        setUsage("/setspawn");
        setPermission("basics.command.setspawn");

        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Placeholders.parse(Messages.NO_PERMISSION + "", sender, this, args));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Placeholders.parse(Messages.ONLY_PLAYERS + ""));
            return true;
        }

        Player player = (Player) sender;

        plugin.getConfigManager().getConfig().set(
                "Spawn-Location",
                player.getLocation().getWorld().getName()
                        + ", " + Math.round(player.getLocation().getX())
                        + ", " + Math.round(player.getLocation().getY())
                        + ", " + Math.round(player.getLocation().getZ())
                        + ", " + Math.round(player.getLocation().getYaw())
                        + ", " + Math.round(player.getLocation().getPitch())
        );
        plugin.getConfigManager().saveConfig();
        player.sendMessage(Placeholders.parse(Messages.SPAWN_SET + "", player, this, args));

        return true;
    }

}
