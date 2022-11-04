package me.blurmit.basics.command.defined;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.lang.Messages;
import me.blurmit.basics.util.placeholder.Placeholders;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class EnchantCommand extends CommandBase {

    private final Basics plugin;

    public EnchantCommand(Basics plugin) {
        super(plugin.getName());
        setName("enchant");
        setDescription("Enchants an item that the sender is holding");
        setAliases(Collections.singletonList("ench"));
        setUsage("/enchant <enchantment> <level>");
        setPermission("basics.commands.enchant");

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

        if (args.length != 2) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.INVALID_ARGS + "", sender, this, args));
            return true;
        }

        Enchantment enchantment;
        Player player = (Player) sender;
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        enchantment = Enchantment.getByKey(NamespacedKey.minecraft(args[0].toLowerCase()));
        int level;

        // Parse the level argument as an integer to make sure it's a valid level
        try {
            level = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(Placeholders.parsePlaceholder(Messages.NUMBER_INVALID + "", args[1]));
            return true;
        }

        if (itemStack.getItemMeta() == null) {
            player.sendMessage(Placeholders.parsePlaceholder(Messages.ENCHANT_INVALID_ITEM + "", player, this, args));
            return true;
        }

        if (enchantment == null) {
            player.sendMessage(Placeholders.parsePlaceholder(Messages.ENCHANT_UNKNOWN + "", sender, this, args));
            return true;
        }

        itemStack.addUnsafeEnchantment(enchantment, level);
        player.sendMessage(Placeholders.parsePlaceholder(Messages.ENCHANT_SUCCESS + "", player, this, args));

        return true;
    }

}
