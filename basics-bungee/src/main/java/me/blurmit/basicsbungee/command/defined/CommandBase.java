package me.blurmit.basicsbungee.command.defined;

import me.blurmit.basicsbungee.BasicsBungee;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Command;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public abstract class CommandBase extends Command {

    private final BasicsBungee plugin;
    private String permission;
    private String[] aliases;
    private long cooldown;

    protected CommandBase(BasicsBungee plugin, @NotNull String name) {
        super(name);

        this.plugin = plugin;
        this.permission = "bungeecord.command.default";
        this.aliases = new String[0];
        this.cooldown = 0;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getPermissionMessage());
            return;
        }

        dispatch(sender, args);
    }

    public abstract void dispatch(CommandSender sender, String[] args);

    public void registerCommand() {
        plugin.getCommandManager().register(this);
    }

    public void setAliases(String... aliases) {
        this.aliases = aliases;

        try {
            Field field = Command.class.getDeclaredField("aliases");
            field.setAccessible(true);
            field.set(this, aliases);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    public void setPermission(String permission) {
        this.permission = permission;

        try {
            Field field = Command.class.getDeclaredField("permission");
            field.setAccessible(true);
            field.set(this, permission);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String[] getAliases() {
        return aliases;
    }

    @Override
    public String getPermission() {
        return permission;
    }

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
    }

    public long getCooldown() {
        return cooldown;
    }

}
