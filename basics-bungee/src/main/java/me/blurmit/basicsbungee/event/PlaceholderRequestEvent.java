package me.blurmit.basicsbungee.event;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Event;

public class PlaceholderRequestEvent extends Event implements Cancellable {

    private final String placeholder;
    private final Command command;
    private final String[] arguments;
    private final Object[] replacements;
    private boolean cancelled;
    private ProxiedPlayer player = null;
    private CommandSender sender = null;
    private String response;

    public PlaceholderRequestEvent(String placeholder, ProxiedPlayer player, Command command, String[] arguments, Object... replacements) {
        this.placeholder = placeholder;
        this.cancelled = false;
        this.player = player;
        this.response = "";
        this.command = command;
        this.arguments = arguments;
        this.replacements = replacements;
    }

    public PlaceholderRequestEvent(String placeholder, CommandSender sender, Command command, String[] arguments, Object... replacements) {
        this.placeholder = placeholder;
        this.cancelled = false;
        this.sender = sender;
        this.response = "";
        this.command = command;
        this.arguments = arguments;
        this.replacements = replacements;
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
    public ProxiedPlayer getPlayer() {
        return player;
    }

    /**
     * @return The sender in which the placeholder is requesting the data from.
     */
    public CommandSender getSender() {
        return sender;
    }

    /**
     * @return The command in which the placeholder is requesting the data from.
     */
    public Command getCommand() {
        return command;
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

}
