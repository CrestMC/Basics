package me.blurmit.basics.command.defined;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.Placeholders;
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
        setUsage("/message <player> <message>");
        setPermission("basics.command.message");
        setAliases("msg", "tell", "whisper", "w");

        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Placeholders.parse(Messages.NO_PERMISSION + "", sender, this, args));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Placeholders.parse(Messages.ONLY_PLAYERS + "", sender, this, args));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Placeholders.parse(Messages.INVALID_ARGS + "", sender, this, args));
            return true;
        }

        Player player = (Player) sender;
        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage(Placeholders.parse(Messages.PLAYER_NOT_FOUND + "", args[0]));
            return true;
        }

        if (!player.canSee(target)) {
            sender.sendMessage(Placeholders.parse(Messages.PLAYER_NOT_FOUND + "", args[0]));
            return true;
        }

        boolean isToggled = Boolean.parseBoolean(target.getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin, "messages-toggled"), PersistentDataType.STRING, ""));

        if (isToggled && !sender.hasPermission("basics.messagetoggle.bypass")) {
            sender.sendMessage(Placeholders.parse(Messages.MESSAGES_TOGGLED_ERROR + "", sender, this, args));
            return true;
        }

        String message = Placeholders.parse(String.join(" ", Arrays.copyOfRange(args, 1, args.length)), sender, this, args);
        NamespacedKey lastMessageKey = new NamespacedKey(plugin, "last-message");

        sender.sendMessage(Placeholders.parse(Messages.MESSAGE_SEND + "", target, this, null, args, false, message));
        target.sendMessage(Placeholders.parse(Messages.MESSAGE_RECEIVE + "", player, this, null, args, false, message));

        // Set the name of the target that the sender last messaged in the sender's metadata
        ((Player) sender).getPersistentDataContainer().set(lastMessageKey, PersistentDataType.STRING, target.getName());
        target.getPersistentDataContainer().set(lastMessageKey, PersistentDataType.STRING, sender.getName());

        target.playSound(target.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 35, 1);
        return true;
    }

}
