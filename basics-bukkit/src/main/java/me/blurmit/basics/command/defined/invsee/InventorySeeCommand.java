package me.blurmit.basics.command.defined.invsee;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class InventorySeeCommand extends CommandBase {

    private final Basics plugin;
    private final InventorySeeListener listener;

    public InventorySeeCommand(Basics plugin) {
        super(plugin.getName());
        setName("invsee");
        setDescription("Opens the inventory of another player");
        setUsage("/invsee <player>");
        setPermission("basics.command.invsee");
        setAliases(Arrays.asList("inventorysee", "viewinv", "viewinventory", "openinv", "openinventory", "showinv", "showinventory"));

        this.plugin = plugin;
        this.listener = new InventorySeeListener(plugin);

        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    @Override
    public boolean execute(CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.NO_PERMISSION + ""));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.ONLY_PLAYERS + ""));
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 1) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.INVALID_ARGS + "", sender, this, args));
            return true;
        }

        Player target = plugin.getServer().getPlayer(args[0]);

        if (target == null || !player.canSee(target)) {
            player.sendMessage(Placeholders.parsePlaceholder(Messages.PLAYER_NOT_FOUND + "", args[0]));
            return true;
        }

        player.openInventory(target.getInventory());
        listener.getInventoryViewers().add(player.getUniqueId());

        player.sendMessage(Placeholders.parsePlaceholder(Messages.OPENED_INVENTORY + "", target, this, args));
        return true;

    }

}
