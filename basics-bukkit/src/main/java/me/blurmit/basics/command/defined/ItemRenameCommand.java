package me.blurmit.basics.command.defined;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.lang.Messages;
import me.blurmit.basics.util.placeholder.Placeholders;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ItemRenameCommand extends CommandBase {

    private final Basics plugin;

    public ItemRenameCommand(Basics plugin) {
        super(plugin.getName());
        setName("itemrename");
        setDescription("Echo a message back to the sender");
        setUsage("/itemrename <name>");
        setPermission("basics.commands.itemrename");

        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.NO_PERMISSION + "", sender, this, args));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.INVALID_ARGS + "", sender, this, args));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.INVALID_ARGS + "", sender, this, args));
            return true;
        }

        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();

        if (item.getItemMeta() == null) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.ITEM_RENAME_INVALID + "", player, this, args));
            return true;
        }

        String name = ChatColor.translateAlternateColorCodes('&', String.join(" ", Arrays.copyOfRange(args, 0, args.length)));
        meta.setDisplayName(name);
        item.setItemMeta(meta);

        player.sendMessage(Placeholders.parsePlaceholder(Messages.ITEM_RENAMED + "", player, this, args));
        return true;

    }

}
