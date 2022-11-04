package me.blurmit.basics.command.defined;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.lang.Messages;
import me.blurmit.basics.util.placeholder.Placeholders;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WalkSpeedCommand extends CommandBase {

    private final Basics plugin;

    public WalkSpeedCommand(Basics plugin) {
        super(plugin.getName());
        setName("walkspeed");
        setDescription("Changes the walk speed of a player");
        setUsage("/walkspeed <number> [player]");
        setPermission("basics.commands.walkspeed");

        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.NO_PERMISSION + "", sender, this, args));
            return true;
        }

        if (args.length == 2) {
            if (!sender.hasPermission("basics.commands.walkspeed.other")) {
                sender.sendMessage(Placeholders.parsePlaceholder(Messages.NO_PERMISSION_SUBCOMMAND + "", sender, this, args));
                return true;
            }

            Player target = plugin.getServer().getPlayer(args[1]);
            float speed;

            if (target == null) {
                sender.sendMessage(Placeholders.parsePlaceholder(Messages.PLAYER_NOT_FOUND + "", args[1]));
                return true;
            }

            try {
                speed = Float.parseFloat(args[0]);
            } catch (IllegalArgumentException e) {
                sender.sendMessage(Placeholders.parsePlaceholder(Messages.NUMBER_INVALID + "", args[0]));
                return true;
            }

            if (speed > 10F || speed < -10F) {
                sender.sendMessage(Placeholders.parsePlaceholder(Messages.FLY_SPEED_INVALID + "", target, this, args));
                return true;
            }

            target.setWalkSpeed(speed / 10F);
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.WALK_SPEED_CHANGED + "", target, this, args));
            return true;

        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.INVALID_ARGS + "", sender, this, args));
            return true;
        }

        if (args.length == 1) {
            Player player = (Player) sender;
            float speed;

            try {
                speed = Float.parseFloat(args[0]);
            } catch (Exception e) {
                sender.sendMessage(Placeholders.parsePlaceholder(Messages.NUMBER_INVALID + "", args[0]));
                return true;
            }

            if (speed > 10F || speed < -10F) {
                sender.sendMessage(Placeholders.parsePlaceholder(Messages.WALK_SPEED_INVALID + "", player, this, args));
                return true;
            }

            player.setWalkSpeed(speed / 10F);
            player.sendMessage(Placeholders.parsePlaceholder(Messages.WALK_SPEED_CHANGED + "", player, this, args));
            return true;
        }

        sender.sendMessage(Placeholders.parsePlaceholder(Messages.INVALID_ARGS + "", sender, this, args));
        return true;

    }

}
