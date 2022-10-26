package me.blurmit.basics.command.defined;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.Gamemodes;
import me.blurmit.basics.util.placeholder.Placeholders;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class GamemodeCommand extends CommandBase {

    private final Basics plugin;

    public GamemodeCommand(Basics plugin) {
        super(plugin.getName());
        setName("gamemode");
        setDescription("Changes the gamemode of a player");
        setAliases(Collections.singletonList("gm"));
        setUsage("Usage: /gamemode <gamemode> [player]");
        setPermission("basics.commands.gamemode");

        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.NO_PERMISSION + "", sender, this, args));
            return true;
        }

        if (args.length == 2) {
            if (!sender.hasPermission("basics.commands.gamemode.other")) {
                sender.sendMessage(Placeholders.parsePlaceholder(Messages.NO_PERMISSION_SUBCOMMAND + "", sender, this, args));
                return true;
            }

            Player target = plugin.getServer().getPlayer(args[1]);
            GameMode gameMode = Gamemodes.getGamemode(args[0]);

            if (target == null) {
                sender.sendMessage(Placeholders.parsePlaceholder(Messages.PLAYER_NOT_FOUND + "", args[1]));
                return true;
            }

            if (gameMode == null) {
                sender.sendMessage(Placeholders.parsePlaceholder(Messages.GAMEMODE_UNKNOWN + "", target, this, args));
                return true;
            }

            target.setGameMode(gameMode);
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.GAMEMODE_CHANGED + "", target, this, args));

            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.INVALID_ARGS + "", sender, this, args));
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 1) {
            GameMode gameMode = Gamemodes.getGamemode(args[0]);

            if (gameMode == null) {
                player.sendMessage(Placeholders.parsePlaceholder(Messages.GAMEMODE_UNKNOWN + "", player, this, args));
                return true;
            }

            player.setGameMode(gameMode);
            player.sendMessage(Placeholders.parsePlaceholder(Messages.GAMEMODE_CHANGED + "", player, this, args));

            return true;
        }

        sender.sendMessage(Placeholders.parsePlaceholder(Messages.INVALID_ARGS + "", sender, this, args));
        return true;

    }

}
