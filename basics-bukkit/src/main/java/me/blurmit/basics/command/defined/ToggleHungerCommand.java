package me.blurmit.basics.command.defined;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ToggleHungerCommand extends CommandBase implements Listener {

    private final Basics plugin;
    private boolean isHungerToggled;

    public ToggleHungerCommand (Basics plugin) {
        super (plugin.getName());
        setName("togglehunger");
        setDescription("Toggle losing hunger.");
        setUsage("/togglehunger");
        setPermission("basics.command.togglehunger");
        setAliases(Arrays.asList("hungertoggle", "th"));

        this.plugin = plugin;
        this.isHungerToggled = plugin.getConfigManager().getConfig().getBoolean("hunger-disabled");

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        isHungerToggled = !isHungerToggled;

        plugin.getConfigManager().getConfig().set("hunger-disabled", isHungerToggled);
        sender.sendMessage(Placeholders.parsePlaceholder(Messages.HUNGER_TOGGLED + "", plugin.getConfigManager().getLanguageConfig().getString("togglehunger." + (isHungerToggled ? "enabled" : "disabled"))));
        return true;
    }

    @EventHandler
    public void onHungerChange(FoodLevelChangeEvent event) {
        if (isHungerToggled) {
            event.setFoodLevel(20);
        }
    }
}
