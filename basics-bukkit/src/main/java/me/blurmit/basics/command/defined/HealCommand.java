package me.blurmit.basics.command.defined;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class HealCommand extends CommandBase {

    private final Basics plugin;

    public HealCommand(Basics plugin) {
        super(plugin.getName());
        setName("heal");
        setDescription("Heal yourself or another player");
        setAliases(Collections.singletonList("healme"));
        setUsage("/heal [player]");
        setPermission("basics.command.heal");

        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.NO_PERMISSION + "", sender, this, args));
            return true;
        }

        if (args.length >= 1) {
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                sender.sendMessage(Placeholders.parsePlaceholder(Messages.PLAYER_NOT_FOUND + "", args[0]));
                return true;
            }

            target.setHealth(target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            target.setSaturation(20);
            target.setFoodLevel(20);

            sender.sendMessage(Placeholders.parsePlaceholder(Messages.PLAYER_HEALED + "", target, this, args));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.INVALID_ARGS + "", sender, this, args));
            return true;
        }

        Player player = (Player) sender;

        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        player.setSaturation(20);
        player.setFoodLevel(20);

        sender.sendMessage(Placeholders.parsePlaceholder(Messages.PLAYER_HEALED + "", player, this, args));
        return true;
    }
}
