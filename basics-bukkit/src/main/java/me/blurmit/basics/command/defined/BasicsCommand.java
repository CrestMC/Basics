package me.blurmit.basics.command.defined;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.scoreboard.Scoreboards;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class BasicsCommand extends CommandBase {

    private final Basics plugin;

    public BasicsCommand(Basics plugin) {
        super(plugin.getName());
        setName("basics");
        setDescription("The main command for the basics plugin");
        setUsage("/basics <reload|info>");
        setPermission("basics.command.basics");

        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.NO_PERMISSION + "", sender, this, args));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.INVALID_ARGS + "", sender, this, args));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload": {
                plugin.getConfigManager().reloadConfig();
                plugin.getConfigManager().reloadScoreboardConfig();
                plugin.getConfigManager().reloadLanguageConfig();
                plugin.getConfigManager().reloadRanksConfig();
                plugin.getScoreboardManager().getBoards().forEach(Scoreboards::reload);
                sender.sendMessage(Placeholders.parsePlaceholder(Messages.PLUGIN_RELOADED + ""));
                return true;
            }
            case "info": {
                sender.sendMessage(Placeholders.parsePlaceholder(Messages.PLUGIN_INFO + "", plugin.getDescription().getVersion(), plugin.getDescription().getAuthors().get(0), ""));
                return true;
            }
            default: {
                sender.sendMessage(Placeholders.parsePlaceholder(Messages.INVALID_ARGS + "", sender, this, args));
                return true;
            }
        }
    }

}
