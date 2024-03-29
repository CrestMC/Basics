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
    private boolean isHungerDisabled;

    public ToggleHungerCommand (Basics plugin) {
        super (plugin.getName());
        setName("togglehunger");
        setDescription("Toggle losing hunger.");
        setUsage("/togglehunger");
        setPermission("basics.command.togglehunger");
        setAliases(Arrays.asList("hungertoggle", "th"));

        this.plugin = plugin;
        this.isHungerDisabled = plugin.getConfigManager().getConfig().getBoolean("Hunger-Disabled");

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        isHungerDisabled = !isHungerDisabled;

        plugin.getConfigManager().getConfig().set("Hunger-Disabled", isHungerDisabled);
        sender.sendMessage(Placeholders.parse(
                Messages.HUNGER_TOGGLED + "", plugin.getConfigManager().getLanguageConfig().getString("togglehunger." + (isHungerDisabled ? "disabled" : "enabled"))
        ));
        return true;
    }

    @EventHandler
    public void onHungerChange(FoodLevelChangeEvent event) {
        if (isHungerDisabled) {
            event.setFoodLevel(20);
        }
    }
}
