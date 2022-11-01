package me.blurmit.basics.command.defined;

import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.lang.Messages;
import me.blurmit.basics.util.placeholder.Placeholders;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExhaustionEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ToggleHungerCommand extends CommandBase implements Listener {

    private final Basics plugin;

    private boolean currentToggled;

    public ToggleHungerCommand (Basics plugin){
        super (plugin.getName());
        setName("togglehunger");
        setDescription("Toggle losing hunger.");
        setUsage("/togglehunger");
        setPermission("basics.command.togglehunger");
        setAliases(Arrays.asList("hunger", "th"));

        this.plugin = plugin;

        this.currentToggled = plugin.getConfigManager().getConfig().getBoolean("hunger-disabled");

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
        if(!currentToggled) {
            plugin.getConfigManager().getConfig().set("hunger-disabled", true);
            commandSender.sendMessage(Placeholders.parsePlaceholder(Messages.HUNGER_TOGGLED + "", plugin.getConfigManager().getLanguageConfig().getString("togglehunger.enabled")));
        } else {
            plugin.getConfigManager().getConfig().set("hunger-disabled", false);
            commandSender.sendMessage(Placeholders.parsePlaceholder(Messages.HUNGER_TOGGLED + "", plugin.getConfigManager().getLanguageConfig().getString("togglehunger.disabled")));
        }
        return true;

    }

    @EventHandler
    public void onHungerChange(FoodLevelChangeEvent e){
        if(plugin.getConfigManager().getConfig().getBoolean("hunger-disabled")){
            e.setFoodLevel(20);
        }
    }
}
