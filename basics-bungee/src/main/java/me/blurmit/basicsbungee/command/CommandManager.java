package me.blurmit.basicsbungee.command;

import me.blurmit.basicsbungee.BasicsBungee;
import me.blurmit.basicsbungee.command.defined.CommandBase;
import me.blurmit.basicsbungee.util.ReflectionUtil;

public class CommandManager {

    private final BasicsBungee plugin;

    public CommandManager(BasicsBungee plugin) {
        this.plugin = plugin;
    }

    public void registerCommands() {
        ReflectionUtil.consume("me.blurmit.basicsbungee.command.defined", BasicsBungee.class.getClassLoader(), CommandBase.class, CommandBase::registerCommand, true, plugin);
    }

    public void register(CommandBase command) {
        plugin.getProxy().getPluginManager().registerCommand(plugin, command);
    }

}
