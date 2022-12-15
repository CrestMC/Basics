package me.blurmit.basics.command.defined;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class SubCommand {

    private String name;
    private String usage;
    private String permission;
    private String description;
    private final Command command;

    protected SubCommand(String name, Command command) {
        this(name, name, "/", "", command);
    }

    protected SubCommand(String name, String description, String usage, String permission, Command command) {
        this.name = name;
        this.usage = usage;
        this.permission = permission;
        this.command = command;
        this.description = description;
    }

    /**
     * Executes the subcommand
     *
     * @param sender Source object which is executing this subcommand
     * @param command The apex command used when executing this subcommand
     * @param args All arguments passed to the command, split via ' '
     */
    public abstract void execute(CommandSender sender, Command command, String[] args);

    public List<String> getTabCompletion() {
        return null;
    }

    /**
     *
     * @param name Sets the name of this subcommand
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return Returns the name of this subcommand
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     *
     * @param description Sets the description of this subcommand
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return Returns the description of this subcommand
     */
    @NotNull
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param usage Sets the usage of this subcommand
     */
    public void setUsage(String usage) {
        this.usage = usage;
    }

    /**
     *
     * @return Returns the usage of this subcommand
     */
    @NotNull
    public String getUsage() {
        return usage;
    }

    /**
     *
     * @param permission Sets the permission of this subcommand
     */
    public void setPermission(String permission) {
        this.permission = permission;
    }

    /**
     *
     * @return Returns the name of this subcommand
     */
    @NotNull
    public String getPermission() {
        return permission;
    }

    /**
     *
     * @return Returns the apex command used when dispatching this subcommand
     */
    @NotNull
    public Command getCommand() {
        return command;
    }

}
