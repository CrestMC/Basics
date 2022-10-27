package me.blurmit.basics.command.defined;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.placeholder.Placeholders;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemLoreCommand extends CommandBase {

    private final Basics plugin;

    public ItemLoreCommand(Basics plugin) {
        super(plugin.getName());
        setName("itemlore");
        setDescription("Changes the lore of an item");
        setUsage("/itemlore <add|set|clear> [line number] [lore]");
        setPermission("basics.commands.itemlore");

        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.NO_PERMISSION + "", sender, this, args));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.ONLY_PLAYERS + "", sender, this, args));
            return true;
        }

        Player player = (Player) sender;

        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();

        List<String> lore;
        String loreText;

        if (args.length < 1) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.INVALID_ARGS + "", player, this, args));
            return true;
        }

        if (item.getType().isAir()) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.ITEM_LORE_ITEM_INVALID + "", player, this, args));
            return true;
        }

        if (args[0].equalsIgnoreCase("clear")) {
            meta.setLore(null);
            item.setItemMeta(meta);
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.ITEM_LORE_CLEARED + "", player, this, args));
            return true;
        } else if (args.length == 1) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.INVALID_ARGS + "", player, this, args));
            return true;
        }

        if (!meta.hasLore()) {
            lore = new ArrayList<>();
        } else {
            lore = meta.getLore();
        }

        switch (args[0]) {
            case "add":
                loreText = ChatColor.translateAlternateColorCodes('&', ChatColor.WHITE + String.join(" ", Arrays.copyOfRange(args, 1, args.length)));

                lore.add(loreText);
                meta.setLore(lore);
                item.setItemMeta(meta);

                player.sendMessage(Placeholders.parsePlaceholder(Messages.ITEM_LORE_ADDED + "", player, this, args));
                return true;
            case "set":
                loreText = ChatColor.translateAlternateColorCodes('&', ChatColor.WHITE + String.join(" ", Arrays.copyOfRange(args, 2, args.length)));
                int line;

                try {
                    line = Integer.parseInt(args[1]);
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(Placeholders.parsePlaceholder(Messages.NUMBER_INVALID + "", args[1]));
                    return true;
                }

                try {
                    lore.set(line, loreText);
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                } catch (IndexOutOfBoundsException e) {
                    sender.sendMessage(Placeholders.parsePlaceholder(Messages.ITEM_LORE_LINE_INVALID + "", player, this, args));
                    return true;
                }

                player.sendMessage(Placeholders.parsePlaceholder(Messages.ITEM_LORE_SET + "", player, this, args));
                return true;
            default:
                sender.sendMessage(Placeholders.parsePlaceholder(Messages.INVALID_ARGS + "", player, this, args));
                return true;
        }

    }

}
