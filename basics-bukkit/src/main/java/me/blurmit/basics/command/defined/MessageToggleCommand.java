package me.blurmit.basics.command.defined;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.placeholder.Placeholders;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class MessageToggleCommand extends CommandBase {

    private final Basics plugin;

    public MessageToggleCommand(Basics plugin) {
        super(plugin.getName());
        setName("messagetoggle");
        setDescription("Toggles your messages on/off");
        setUsage("/messagetoggle");
        setAliases(Arrays.asList("msgtoggle", "tpm", "tm", "toggleprivatemessages", "toggledirectmessages"));
        setPermission("basics.commands.messagetoggle");

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

        NamespacedKey key = new NamespacedKey(plugin, "messages-toggled");
        Player player = (Player) sender;

        boolean isToggled = Boolean.parseBoolean(player.getPersistentDataContainer().getOrDefault(key, PersistentDataType.STRING, ""));
        player.getPersistentDataContainer().set(key, PersistentDataType.STRING, !isToggled + "");

        player.sendMessage(Placeholders.parsePlaceholder(Messages.MESSAGES_TOGGLED + "", isToggled ? "on" : "off"));
        return true;
    }

}
