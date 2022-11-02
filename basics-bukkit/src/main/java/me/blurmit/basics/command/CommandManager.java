package me.blurmit.basics.command;

import me.blurmit.basics.Basics;
import me.blurmit.basics.util.Reflector;
import me.blurmit.basics.util.lang.Messages;
import me.blurmit.basics.util.lang.Prefixes;
import me.blurmit.basics.util.placeholder.Placeholders;
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
        Reflector.consume("me.blurmit.basics.command.defined", Basics.class.getClassLoader(), CommandBase.class, CommandBase::registerCommand, true, plugin);
    }

    public void register(String command, Command commandClass) {
        // Check if the server's plugin manager is SimplePluginManager
        if (!(plugin.getServer().getPluginManager() instanceof SimplePluginManager)) {
            plugin.getLogger().severe(Prefixes.ERROR + "Could not register command " + command + ": SimplePluginManager not found!");
            return;
        }

        final Map<?, ?> knownCommandsMap;

        try {
            // Get the commandmap field
            Field commandMapField = SimplePluginManager.class.getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            commandMap = (SimpleCommandMap) commandMapField.get(plugin.getServer().getPluginManager());

            // Get the direct hashmap of known commands
            Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);
            knownCommandsMap = (Map<?, ?>) knownCommandsField.get(commandMap);

        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, Prefixes.ERROR + "Could not register command " + command + ": " + e.getMessage(), e);
            return;
        }

        // Remove command if it's already registered by another plugin
        knownCommandsMap.remove(command.toLowerCase());
        commandClass.getAliases().forEach(alias -> knownCommandsMap.remove(alias.toLowerCase()));

        // Register command in the command map
        commandMap.register(plugin.getName().toLowerCase(), commandClass);

        // Remove fallback prefix command
        knownCommandsMap.remove(plugin.getName().toLowerCase() + ":" + command);
        commandClass.getAliases().forEach(alias -> knownCommandsMap.remove(plugin.getName().toLowerCase() + ":" + alias.toLowerCase()));

    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Command command = commandMap.getCommand(event.getMessage());

        if (!(command instanceof CommandBase)) {
            return;
        }

        CommandBase commandBase = (CommandBase) commandMap.getCommand(event.getMessage());
        long cooldown = commandBase.getCooldown();

        if (!commandCooldowns.containsKey(event.getPlayer().getUniqueId())) {
            return;
        }

        long secondsLeft = ((commandCooldowns.get(event.getPlayer().getUniqueId()) / 1000) + cooldown) - (System.currentTimeMillis() / 1000);

        if (secondsLeft > 0) {
            event.getPlayer().sendMessage(Placeholders.parsePlaceholder(Messages.COMMAND_COOLDOWN + "", secondsLeft + ""));
            event.setCancelled(true);
            commandCooldowns.remove(event.getPlayer().getUniqueId());
            return;
        }

        commandCooldowns.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

}
