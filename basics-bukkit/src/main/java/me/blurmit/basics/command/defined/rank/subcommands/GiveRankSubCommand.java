package me.blurmit.basics.command.defined.rank.subcommands;

import com.google.gson.JsonParser;
import me.blurmit.basics.Basics;
import me.blurmit.basics.command.defined.SubCommand;
import me.blurmit.basics.rank.Rank;
import me.blurmit.basics.util.placeholder.Placeholders;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class GiveRankSubCommand extends SubCommand {

    private final Basics plugin;
    private final Map<String, UUID> cachedPlayers;

    public GiveRankSubCommand(Basics plugin, Command command) {
        super(plugin.getName(), command);
        setName("give");
        setUsage("/rank give <rank> <player> [server]");
        setPermission("basics.commands.rank.give");
        setDescription("Gives a rank to a player");

        this.plugin = plugin;
        this.cachedPlayers = new HashMap<>();

        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, cachedPlayers::clear, 0L, 10 * 60 * 20L);
    }

    @Override
    public void execute(CommandSender sender, Command command, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.INVALID_ARGS_SUBCOMMAND + "", sender, this, args));
            return;
        }

        if (cachedPlayers.containsKey(args[2].toLowerCase())) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> handleGiveRank(sender, args, cachedPlayers.get(args[2].toLowerCase())));
        } else {
            retrieveUUID(args[1].toLowerCase(), uuid -> handleGiveRank(sender, args, uuid));
        }
    }

    private void handleGiveRank(CommandSender sender, String[] args, UUID uuid) {
        Player target = plugin.getServer().getPlayer(args[2]);
        Rank rank = plugin.getRankManager().getRankByName(args[1]);

        if (uuid == null) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.ACCOUNT_DOESNT_EXIST + "", true, args[2]));
            return;
        }

        if (rank == null) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.RANK_NOT_FOUND + "", true, args[1].toLowerCase()));
            return;
        }

        String server = "global";

        if (args.length > 3) {
            server = args[3];
        }

        cachedPlayers.put(args[1].toLowerCase(), uuid);
        plugin.getRankManager().giveRank(args[1], uuid.toString(), server);
        sender.sendMessage(Placeholders.parsePlaceholder(Messages.RANK_GRANTED_SUCCESS + "", true, args[2], rank.getDisplayName()));

        if (target != null) {
            target.sendMessage(Placeholders.parsePlaceholder(Messages.RANK_RECEIVED + "", true, rank.getDisplayName()));
        }
    }

    private void retrieveUUID(String username, Consumer<UUID> consumer) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URL apiServer = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
                InputStreamReader uuidReader = new InputStreamReader(apiServer.openStream());
                String uuidString = new JsonParser().parse(uuidReader).getAsJsonObject().get("id").getAsString();
                UUID uuid = UUID.fromString(uuidString.replaceFirst(
                        "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                        "$1-$2-$3-$4-$5"
                ));
                consumer.accept(uuid);
            } catch (IOException | IllegalStateException e) {
                consumer.accept(null);
            }
        });
    }

}
