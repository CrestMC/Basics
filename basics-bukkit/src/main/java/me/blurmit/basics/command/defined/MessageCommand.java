package me.blurmit.basics.command.defined;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.placeholder.Placeholders;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class MessageCommand extends CommandBase {

    private final Basics plugin;

    public MessageCommand(Basics plugin) {
        super(plugin.getName());
        setName("message");
        setDescription("Sends a player a message");
        setUsage("Usage: /message <player> <message>");
        setPermission("basics.commands.message");
        setAliases(Arrays.asList("msg", "tell", "whisper", "w"));

        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.NO_PERMISSION + "", sender, this, args));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.INVALID_ARGS + "", sender, this, args));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.PLAYER_NOT_FOUND + "", args[0]));
            return true;
        }

        if (sender instanceof Player && !((Player) sender).canSee(target)) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.PLAYER_NOT_FOUND + "", args[0]));
            return true;
        }

        String message = Placeholders.parsePlaceholder(String.join(" ", Arrays.copyOfRange(args, 1, args.length)), sender, this, args);

        sender.sendMessage(Placeholders.parsePlaceholder(Messages.MESSAGE_SEND + "", target.getName(), message));
        target.sendMessage(Placeholders.parsePlaceholder(Messages.MESSAGE_RECEIVE + "", sender.getName(), message));

        if (sender instanceof Player) {
            // Set the name of the target that the sender last messaged in the sender's metadata
            ((Player) sender).getPersistentDataContainer().set(new NamespacedKey(plugin, "last-message"), PersistentDataType.STRING, target.getName());
            target.getPersistentDataContainer().set(new NamespacedKey(plugin, "last-message"), PersistentDataType.STRING, sender.getName());
        }

        target.playSound(target.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 35, 1);
        return true;
    }

}
