package me.blurmit.basics.command.defined;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class SpeedCommand extends CommandBase {

    private final Basics plugin;

    public SpeedCommand(Basics plugin) {
        super(plugin.getName());
        setName("speed");
        setDescription("Changes your flying/walking speed based");
        setUsage("/speed <number> [player] [type]");
        setPermission("basics.command.speed");

        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.NO_PERMISSION + ""));
            return true;
        }

        SpeedType type;

        if (sender instanceof Player) {
            Player player = (Player) sender;
            type = player.isFlying() ? SpeedType.FLY : SpeedType.WALK;
        } else {
            type = SpeedType.WALK;
        }

        if (args.length > 2) {
            switch (args[2].toLowerCase()) {
                case "walk":
                case "run":
                    type = SpeedType.WALK;
                    break;
                case "fly":
                    type = SpeedType.FLY;
            }
        }

        String[] newArgs = Arrays.copyOfRange(args, 0, args.length - 1);
        if (type == SpeedType.FLY) {
            new FlySpeedCommand(plugin).execute(sender, commandLabel, newArgs);
        } else {
            new WalkSpeedCommand(plugin).execute(sender, commandLabel, newArgs);
        }
        return true;

    }

    private enum SpeedType {
        WALK,
        FLY
    }

}
