package dev.blurmit.basics.util.lang;

import dev.blurmit.basics.Basics;
import org.jetbrains.annotations.NotNull;

public class Messages {

    public static final String INVALID_ARGS = Basics.getInstance().getConfigManager().getMessages().getString("invalid-args");
    public static final String NO_PERMISSION = Basics.getInstance().getConfigManager().getMessages().getString("no-permission");
    public static final String NO_PERMISSION_SUBCOMMAND = Basics.getInstance().getConfigManager().getMessages().getString("no-permission-subcommand");
    public static final String ONLY_PLAYERS = Basics.getInstance().getConfigManager().getMessages().getString("only-players");
    public static final String PLAYER_NOT_FOUND = Basics.getInstance().getConfigManager().getMessages().getString("player-not-found");
    public static final String NUMBER_INVALID = Basics.getInstance().getConfigManager().getMessages().getString("number-invalid");
    public static final String ENCHANT_SUCCESS = Basics.getInstance().getConfigManager().getMessages().getString("enchant-success");
    public static final String ENCHANT_UNKNOWN = Basics.getInstance().getConfigManager().getMessages().getString("enchant-unknown");
    public static final String ENCHANT_INVALID_ITEM = Basics.getInstance().getConfigManager().getMessages().getString("enchant-invalid-item");
    public static final String FLY_TOGGLE = Basics.getInstance().getConfigManager().getMessages().getString("fly-toggle");
    public static final String FLY_SPEED_CHANGED = Basics.getInstance().getConfigManager().getMessages().getString("fly-speed-changed");
    public static final String FLY_SPEED_INVALID = Basics.getInstance().getConfigManager().getMessages().getString("fly-speed-invalid");
    public static final String WALK_SPEED_CHANGED = Basics.getInstance().getConfigManager().getMessages().getString("walk-speed-changed");
    public static final String WALK_SPEED_INVALID = Basics.getInstance().getConfigManager().getMessages().getString("walk-speed-invalid");
    public static final String GAMEMODE_CHANGED = Basics.getInstance().getConfigManager().getMessages().getString("gamemode-changed");
    public static final String GAMEMODE_UNKNOWN = Basics.getInstance().getConfigManager().getMessages().getString("gamemode-unknown");
    public static final String ITEM_RENAMED = Basics.getInstance().getConfigManager().getMessages().getString("item-renamed");
    public static final String ITEM_RENAME_INVALID = Basics.getInstance().getConfigManager().getMessages().getString("item-rename-invalid");
    public static final String ITEM_LORE_ADDED = Basics.getInstance().getConfigManager().getMessages().getString("item-lore-added");
    public static final String ITEM_LORE_SET = Basics.getInstance().getConfigManager().getMessages().getString("item-lore-set");
    public static final String ITEM_LORE_CLEARED = Basics.getInstance().getConfigManager().getMessages().getString("item-lore-cleared");
    public static final String ITEM_LORE_ITEM_INVALID = Basics.getInstance().getConfigManager().getMessages().getString("item-lore-item-invalid");
    public static final String ITEM_LORE_LINE_INVALID = Basics.getInstance().getConfigManager().getMessages().getString("item-lore-line-invalid");
    public static final String SUDO_SUCCESS = Basics.getInstance().getConfigManager().getMessages().getString("sudo-success");
    public static final String CHAT_CLEARED = Basics.getInstance().getConfigManager().getMessages().getString("chat-cleared");
    public static final String WORLD_TELEPORTED = Basics.getInstance().getConfigManager().getMessages().getString("world-teleported");
    public static final String INVALID_WORLD = Basics.getInstance().getConfigManager().getMessages().getString("invalid-world");
    public static final String SLOWMODE_SET = Basics.getInstance().getConfigManager().getMessages().getString("slowmode-set");
    public static final String SLOWMODE_MESSAGE = Basics.getInstance().getConfigManager().getMessages().getString("slowmode-message");
    public static final String MUTE_CHAT_TOGGLE = Basics.getInstance().getConfigManager().getMessages().getString("mute-chat-toggle");
    public static final String MUTE_CHAT_MESSAGE = Basics.getInstance().getConfigManager().getMessages().getString("mute-chat-message");
    public static final String PING_MESSAGE = Basics.getInstance().getConfigManager().getMessages().getString("ping-message");
    public static final String SPAWN_SET = Basics.getInstance().getConfigManager().getMessages().getString("spawn-set");
    public static final String TELEPORTED_SPAWN = Basics.getInstance().getConfigManager().getMessages().getString("teleported-spawn");
    public static final String INVALID_WORLD_TYPE = Basics.getInstance().getConfigManager().getMessages().getString("invalid-world-type");
    public static final String WORLD_DELETED = Basics.getInstance().getConfigManager().getMessages().getString("world-deleted");
    public static final String WORLD_ALREADY_EXISTS = Basics.getInstance().getConfigManager().getMessages().getString("world-already-exists");
    public static final String PLAYER_HEALED = Basics.getInstance().getConfigManager().getMessages().getString("player-healed");
    public static final String PLAYER_FED = Basics.getInstance().getConfigManager().getMessages().getString("player-fed");
    public static final String WORLD_CREATED = Basics.getInstance().getConfigManager().getMessages().getString("world-created");

    @NotNull
    public static String getFancyName(@NotNull String name) {
        return name.toLowerCase().replace("_", " ");
    }

}
