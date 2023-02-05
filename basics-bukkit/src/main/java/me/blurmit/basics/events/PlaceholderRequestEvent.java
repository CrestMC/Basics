package me.blurmit.basics.events;

import me.blurmit.basics.command.defined.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlaceholderRequestEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final String placeholder;
    private final Command command;
    private final SubCommand subCommand;
    private final String[] arguments;
    private final Object[] replacements;
    private boolean cancelled;
    private Player player = null;
    private CommandSender sender = null;
    private String response;

    public PlaceholderRequestEvent(String placeholder, Player player, Command command, SubCommand subcommand, String[] arguments, Object[] replacements, boolean async) {
        super(async);
        this.placeholder = placeholder;
        this.cancelled = false;
        this.player = player;
        this.response = "";
        this.command = command;
        this.subCommand = subcommand;
        this.arguments = arguments;
        this.replacements = replacements;
    }

    public PlaceholderRequestEvent(String placeholder, CommandSender sender, Command command, SubCommand subCommand, String[] arguments, Object[] replacements, boolean async) {
        super(async);
        this.placeholder = placeholder;
        this.cancelled = false;
        this.sender = sender;
        this.response = "";
        this.command = command;
        this.subCommand = subCommand;
        this.arguments = arguments;
        this.replacements = replacements;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * @return The placeholder that is being requested.
     */
    public String getPlaceholder() {
        return placeholder;
    }

    /**
     * @return The response to the placeholder that is being requested.
     */
    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    /**
     * @return The player in which the placeholder is requesting the data from.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return The sender in which the placeholder is requesting the data from.
     */
    public CommandSender getSender() {
        return sender == null ? player : sender;
    }

    /**
     * @return The command in which the placeholder is requesting the data from.
     */
    public Command getCommand() {
        return command;
    }

    /**
     * @return The subcommand in which the placeholder is requesting the data from.
     */
    public SubCommand getSubCommand() {
        return subCommand;
    }

    /**
     * @return The arguments of the command in which the placeholder is requesting the data from.
     */
    public String[] getArguments() {
        return arguments;
    }

    /**
     * @return The replacement objects in the placeholder.
     */
    public Object[] getReplacements() {
        return replacements;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
