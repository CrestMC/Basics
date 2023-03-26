package me.blurmit.basics.command.defined;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.Booleans;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class FlyCommand extends CommandBase {

    private final Basics plugin;

    public FlyCommand(Basics plugin) {
        super(plugin.getName());
        setName("fly");
        setDescription("Toggles the ability to fly for a player");
        setAliases(Arrays.asList("flight", "flying"));
        setUsage("/fly [boolean] [player]");
        setPermission("basics.command.fly");

        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Placeholders.parse(Messages.NO_PERMISSION + "", sender, this, args));
            return true;
        }

        if (args.length == 2) {
            if (!sender.hasPermission("basics.command.fly.other")) {
                sender.sendMessage(Placeholders.parse(Messages.NO_PERMISSION_SUBCOMMAND + "", sender, this, args));
                return true;
            }

            Player target = plugin.getServer().getPlayer(args[1]);
            boolean state = Booleans.isFancyBoolean(args[0]);

            if (target == null) {
                sender.sendMessage(Placeholders.parse(Messages.PLAYER_NOT_FOUND + "", args[1]));
                return true;
            }

            target.setAllowFlight(state);
            target.sendMessage(Placeholders.parse(Messages.FLY_TOGGLE + "", target, this, args));

            if (!target.equals(sender)) {
                sender.sendMessage(Placeholders.parse(Messages.FLY_TOGGLE + "", target, this, args));
            }

            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Placeholders.parse(Messages.INVALID_ARGS + "", sender, this, args));
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.setAllowFlight(!player.getAllowFlight());
            player.sendMessage(Placeholders.parse(Messages.FLY_TOGGLE + "", player, this, args));
            return true;
        }

        if (args.length == 1) {
            boolean state = Booleans.isFancyBoolean(args[0]);

            player.setAllowFlight(state);
            player.sendMessage(Placeholders.parse(Messages.FLY_TOGGLE + "", player, this, args));
            return true;
        }

        sender.sendMessage(Placeholders.parse(Messages.INVALID_ARGS + "", player, this, args));
        return true;

    }

}
