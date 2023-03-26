package me.blurmit.basicsbungee.command.defined;

import me.blurmit.basicsbungee.BasicsBungee;
import me.blurmit.basicsbungee.util.Messages;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class LimboCommand extends Command {

    private BasicsBungee plugin;

    public LimboCommand(BasicsBungee plugin) {
        super("limbo", "basics.command.limbo");

        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(Messages.ONLY_PLAYERS.text());
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;

        plugin.getLimboManager().banishToLimbo(player);
    }

}
