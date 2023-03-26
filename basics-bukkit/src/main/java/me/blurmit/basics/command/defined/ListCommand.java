package me.blurmit.basics.command.defined;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.RankUtil;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

public class ListCommand extends CommandBase {

    private final Basics plugin;

    public ListCommand(Basics plugin) {
        super(plugin.getName());
        setName("list");
        setDescription("Sends a list of all online players, including their rank");
        setUsage("/list");
        setPermission("basics.command.list");
        setAliases(Arrays.asList("who", "online"));

        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Placeholders.parse(Messages.NO_PERMISSION + "", sender, this, args));
            return true;
        }

        String players = plugin.getServer().getOnlinePlayers().stream()
                .filter(onlinePlayer -> {
                    if (!(sender instanceof Player)) {
                        return true;
                    }

                    Player player = (Player) sender;
                    return player.canSee(onlinePlayer);
                })
                .sorted(Comparator.comparingInt(onlinePlayer -> -RankUtil.getPriority(onlinePlayer.getUniqueId())))
                .map(onlinePlayer -> RankUtil.getColoredName(onlinePlayer.getUniqueId()))
                .collect(Collectors.joining(ChatColor.GRAY + ", " + ChatColor.RESET, ChatColor.GRAY + "", ChatColor.RESET + ""));
        String ranks = RankUtil.getOrderedRanks();

        sender.sendMessage(Placeholders.parse(Messages.LIST_MESSAGE + "", sender, this, null, args, false, ranks, players));
        return true;
    }

}
