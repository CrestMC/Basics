package me.blurmit.basics.command;

import me.blurmit.basics.Basics;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.ReflectionUtil;
import me.blurmit.basics.util.lang.Messages;
import me.blurmit.basics.util.lang.Prefixes;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class CommandManager implements Listener {

    private final Basics plugin;
    private SimpleCommandMap commandMap;
    private final Map<UUID, Long> commandCooldowns;

    public CommandManager(Basics plugin) {
        this.plugin = plugin;
        this.commandCooldowns = new HashMap<>();

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void registerCommands() {
        ReflectionUtil.consume("me.blurmit.basics.command.defined", Basics.class.getClassLoader(), CommandBase.class, CommandBase::registerCommand, true, plugin);
    }

    public void register(CommandBase command) {
        // Check if the server's plugin manager is SimplePluginManager
        if (!(plugin.getServer().getPluginManager() instanceof SimplePluginManager)) {
            plugin.getLogger().severe(Prefixes.ERROR + "Could not register command " + command.getName() + ": SimplePluginManager not found!");
            return;
        }

        final Map<String, Command> knownCommandsMap;

        try {
            // Get the commandmap field
            Field commandMapField = SimplePluginManager.class.getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            commandMap = (SimpleCommandMap) commandMapField.get(plugin.getServer().getPluginManager());

            // Get the direct hashmap of known commands
            Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);
            knownCommandsMap = (Map<String, Command>) knownCommandsField.get(commandMap);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, Prefixes.ERROR + "Could not register command " + command + ": " + e.getMessage(), e);
            return;
        }

        // Remove command if it's already registered by another plugin
        knownCommandsMap.remove(command.getName().toLowerCase());
        command.getAliases().forEach(alias -> knownCommandsMap.remove(alias.toLowerCase()));

        // Register command in the command map
        commandMap.register(command.getName(), plugin.getName().toLowerCase(), command);

        // Remove fallback prefix command
        knownCommandsMap.remove(plugin.getName().toLowerCase() + ":" + command.getName());
        command.getAliases().forEach(alias -> knownCommandsMap.remove(plugin.getName().toLowerCase() + ":" + alias.toLowerCase()));
    }

    public void unregister(CommandBase command) {
        // Check if the server's plugin manager is SimplePluginManager
        if (!(plugin.getServer().getPluginManager() instanceof SimplePluginManager)) {
            plugin.getLogger().severe(Prefixes.ERROR + "Could not register command " + command + ": SimplePluginManager not found!");
            return;
        }

        final Map<String, Command> knownCommandsMap;

        try {
            // Get the commandmap field
            Field commandMapField = SimplePluginManager.class.getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            commandMap = (SimpleCommandMap) commandMapField.get(plugin.getServer().getPluginManager());

            // Get the direct hashmap of known commands
            Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);
            knownCommandsMap = (Map<String, Command>) knownCommandsField.get(commandMap);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, Prefixes.ERROR + "Could not unregister command " + command.getName() + ": " + e.getMessage(), e);
            return;
        }

        // Remove command if it's already registered by another plugin
        knownCommandsMap.remove(command.getName().toLowerCase());
        command.getAliases().forEach(alias -> knownCommandsMap.remove(alias.toLowerCase()));
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().split("/").length == 0) {
            return;
        }

        String commandLabel = event.getMessage().split("/")[1].split(" ")[0];
        Command command = commandMap.getCommand(commandLabel);

        if (!(command instanceof CommandBase)) {
            return;
        }

        if (event.getMessage().split(" ").length < 2) {
            return;
        }

        CommandBase commandBase = (CommandBase) commandMap.getCommand(commandLabel);
        long cooldown = commandBase.getCooldown();

        if (cooldown == 0) {
            return;
        }

        if (commandCooldowns.containsKey(event.getPlayer().getUniqueId())) {

            long secondsLeft = ((commandCooldowns.get(event.getPlayer().getUniqueId()) / 1000) + cooldown) - (System.currentTimeMillis() / 1000);

            if (secondsLeft > 0) {
                event.getPlayer().sendMessage(Placeholders.parsePlaceholder(Messages.COMMAND_COOLDOWN + "", secondsLeft + ""));
                event.setCancelled(true);
                return;
            }

            commandCooldowns.remove(event.getPlayer().getUniqueId());
        }

        commandCooldowns.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

}
