package me.blurmit.basicsbungee.command;

import me.blurmit.basicsbungee.BasicsBungee;
import me.blurmit.basicsbungee.util.Messages;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ServerAliasCommand extends Command {

    private final BasicsBungee plugin;
    private final String server;

    public ServerAliasCommand(BasicsBungee plugin, String command, String server) {
        super(command);

        this.plugin = plugin;
        this.server = server;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(Messages.ONLY_PLAYERS.text());
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        ServerInfo server = plugin.getProxy().getServerInfo(this.server);

        if (server == null) {
            player.sendMessage(Messages.BROKEN_SERVER.text());
            return;
        }

        if (server.isRestricted() && !player.hasPermission("bungeecord.server." + server.getName())) {
            player.sendMessage(Messages.RESTRICTED_SERVER.text());
            return;
        }

        player.connect(server);
    }

}
