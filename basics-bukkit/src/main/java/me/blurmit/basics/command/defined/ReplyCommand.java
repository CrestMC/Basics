package me.blurmit.basics.command.defined;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.lang.Messages;
import me.blurmit.basics.util.placeholder.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;

public class ReplyCommand extends CommandBase {

    private final Basics plugin;

    public ReplyCommand(Basics plugin) {
        super(plugin.getName());
        setName("reply");
        setDescription("Replys to the lastest message that the sender received");
        setUsage("/reply <message>");
        setPermission("basics.command.reply");
        setAliases(Collections.singletonList("r"));

        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.NO_PERMISSION + "", sender, this, args));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.ONLY_PLAYERS + ""));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.INVALID_ARGS + "", sender, this, args));
            return true;
        }

        Player player = (Player) sender;
        String targetName = player.getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin, "last-message"), PersistentDataType.STRING, "");

        if (targetName.equals("")) {
            player.sendMessage(Placeholders.parsePlaceholder(Messages.REPLY_ERROR + ""));
            return true;
        }

        Player target = Bukkit.getPlayerExact(targetName);

        if (target == null) {
            player.sendMessage(Placeholders.parsePlaceholder(Messages.PLAYER_NOT_FOUND + "", targetName));
            return true;
        }

        if (!player.canSee(target)) {
            player.sendMessage(Placeholders.parsePlaceholder(Messages.PLAYER_NOT_FOUND + "", targetName));
            return true;
        }

        boolean isToggled = Boolean.parseBoolean(target.getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin, "messages-toggled"), PersistentDataType.STRING, ""));

        if (isToggled && !sender.hasPermission("basics.messagetoggle.bypass")) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.MESSAGES_TOGGLED_ERROR + "", sender, this, args));
            return true;
        }

        String message = Placeholders.parsePlaceholder(String.join(" ", Arrays.copyOfRange(args, 0, args.length)), player, this, args);

        player.sendMessage(Placeholders.parsePlaceholder(Messages.MESSAGE_SEND + "", target, this, null, args, false, message));
        target.sendMessage(Placeholders.parsePlaceholder(Messages.MESSAGE_RECEIVE + "", player, this, null, args, false, message));

        target.playSound(target.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 35, 1);
        return true;
    }

}
