package me.blurmit.basics.command.defined.rank;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.command.defined.SubCommand;
import me.blurmit.basics.command.defined.rank.subcommands.*;
import me.blurmit.basics.util.placeholder.Placeholders;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class RankCommand extends CommandBase {

    private final Basics plugin;
    private final Map<String, SubCommand> subCommands;

    public RankCommand(Basics plugin) {
        super(plugin.getName());
        setName("rank");
        setDescription("Gives a player a rank!");
        setPermission("basics.commands.rank");

        this.plugin = plugin;
        this.subCommands = new HashMap<>();

        subCommands.put("create", new CreateRankSubCommand(plugin, this));
        subCommands.put("delete", new DeleteRankSubCommand(plugin, this));
        subCommands.put("edit", new EditRankSubCommand(plugin, this));
        subCommands.put("give", new GiveRankSubCommand(plugin, this));
        subCommands.put("revoke", new RevokeRankSubCommand(plugin, this));
    }

    @Override
    public boolean execute(CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.NO_PERMISSION + "", sender, this, args));
            return true;
        }

        List<String> usages = new ArrayList<>();
        subCommands.values().stream().filter(subcommand -> sender.hasPermission(subcommand.getPermission())).forEach(subcommand -> {
            usages.add(subcommand.getUsage() + "\n  - " + subcommand.getDescription());
        });
        setUsage("\n&c" + String.join("\n", usages));

        if (!(args.length > 0)) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.INVALID_ARGS + "", sender, this, args));
            return true;
        }

        if (!subCommands.containsKey(args[0].toLowerCase())) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.INVALID_ARGS + "", sender, this, args));
            return true;
        }

        SubCommand subCommand = subCommands.get(args[0].toLowerCase());

        if (!sender.hasPermission(subCommand.getPermission())) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.NO_PERMISSION_SUBCOMMAND + "", sender, this, args));
            return true;
        }

        subCommand.execute(sender, this, args);
        return true;
    }

}