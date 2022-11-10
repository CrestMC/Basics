package me.blurmit.basics.util.lang;

import me.blurmit.basics.Basics;
import me.blurmit.basics.util.placeholder.Placeholders;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public enum Messages {

    HUNGER_TOGGLED() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("togglehunger.toggled");
        }
    },

    PLUGIN_INFO() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("plugin-info");
        }
    },

    PLUGIN_RELOADED() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("plugin-reloaded");
        }
    },

    INVALID_ARGS() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("invalid-args");
        }
    },

    INVALID_ARGS_SUBCOMMAND() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("invalid-args-subcommand");
        }
    },

    NO_PERMISSION() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("no-permission");
        }
    },

    NO_PERMISSION_SUBCOMMAND() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("no-permission-subcommand");
        }
    },

    ONLY_PLAYERS() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("only-players");
        }
    },

    PLAYER_NOT_FOUND() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("player-not-found");
        }
    },

    ACCOUNT_DOESNT_EXIST() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("account-doesnt-exist");
        }
    },

    NUMBER_INVALID() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("number-invalid");
        }
    },

    ENCHANT_SUCCESS() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("enchant-success");
        }
    },

    ENCHANT_UNKNOWN() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("enchant-unknown");
        }
    },

    ENCHANT_INVALID_ITEM() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("enchant-invalid-item");
        }
    },

    FLY_TOGGLE() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("fly-toggle");
        }
    },

    FLY_SPEED_CHANGED() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("fly-speed-changed");
        }
    },

    FLY_SPEED_INVALID() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("fly-speed-invalid");
        }
    },

    WALK_SPEED_CHANGED() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("walk-speed-changed");
        }
    },

    WALK_SPEED_INVALID() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("walk-speed-invalid");
        }
    },

    GAMEMODE_CHANGED() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("gamemode-changed");
        }
    },

    GAMEMODE_UNKNOWN() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("gamemode-unknown");
        }
    },

    ITEM_RENAMED() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("item-renamed");
        }
    },

    ITEM_RENAME_INVALID() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("item-rename-invalid");
        }
    },

    ITEM_LORE_ADDED() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("item-lore-added");
        }
    },

    ITEM_LORE_SET() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("item-lore-set");
        }
    },

    ITEM_LORE_CLEARED() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("item-lore-cleared");
        }
    },

    ITEM_LORE_ITEM_INVALID() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("item-lore-item-invalid");
        }
    },

    ITEM_LORE_LINE_INVALID() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("item-lore-line-invalid");
        }
    },

    SUDO_SUCCESS() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("sudo-success");
        }
    },

    CHAT_CLEARED() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("chat-cleared");
        }
    },

    WORLD_TELEPORTED() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("world-teleported");
        }
    },

    INVALID_WORLD() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("invalid-world");
        }
    },

    SLOWMODE_SET() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("slowmode-set");
        }
    },

    SLOWMODE_MESSAGE() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("slowmode-message");
        }
    },

    MUTE_CHAT_TOGGLE() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("mute-chat-toggle");
        }
    },

    MUTE_CHAT_MESSAGE() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("mute-chat-message");
        }
    },

    PING_MESSAGE() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("ping-message");
        }
    },

    SPAWN_SET() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("spawn-set");
        }
    },

    TELEPORTED_SPAWN() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("teleported-spawn");
        }
    },

    INVALID_WORLD_TYPE() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("invalid-world-type");
        }
    },

    WORLD_DELETED() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("world-deleted");
        }
    },

    WORLD_ALREADY_EXISTS() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("world-already-exists");
        }
    },

    PLAYER_HEALED() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("player-healed");
        }
    },

    PLAYER_FED() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("player-fed");
        }
    },

    WORLD_CREATED() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("world-created");
        }
    },

    BROADCAST_MESSAGE() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("broadcast-message");
        }
    },

    PLAYER_TELEPORTED() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("player-teleported");
        }
    },

    PLAYER_TELEPORTED_OTHER() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("player-teleported-other");
        }
    },

    MESSAGE_SEND() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("message-send");
        }
    },

    MESSAGE_RECEIVE() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("message-receive");
        }
    },

    CONNECT_MESSAGE() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("connect-message");
        }
    },

    REPLY_ERROR() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("reply-error");
        }
    },

    RANK_GRANTED_SUCCESS() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("rank-given-success");
        }
    },

    RANK_RECEIVED() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("rank-received");
        }
    },

    RANK_REVOKED_SUCCESS() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("rank-revoked-success");
        }
    },

    RANK_REVOKED() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("rank-revoked");
        }
    },

    RANK_NOT_FOUND() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("rank-not-found");
        }
    },

    RANK_CREATED() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("rank-created");
        }
    },

    RANK_DELETED() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("rank-deleted");
        }
    },

    RANK_PREFIX_SET() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("rank-prefix-set");
        }
    },

    RANK_SUFFIX_SET() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("rank-suffix-set");
        }
    },

    RANK_DEFAULT_SET() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("rank-default-set");
        }
    },

    RANK_PRIORITY_SET() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("rank-priority-set");
        }
    },

    RANK_COLOR_SET() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("rank-color-set");
        }
    },

    RANK_DISPLAYNAME_SET() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("rank-displayname-set");
        }
    },

    RANK_PERMISSION_ADDED() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("rank-permission-added");
        }
    },

    RANK_PERMISSION_REMOVED() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("rank-permission-removed");
        }
    },

    MESSAGES_TOGGLED() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("messages-toggled");
        }
    },

    MESSAGES_TOGGLED_ERROR() {
       @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("messages-toggled-error");
        }
    },

    RANK_ALREADY_EXISTS() {
      @Override
      public String toString() {
          return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("rank-already-exists");
      }
    },

    RANK_ALREADY_OWNED() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("rank-already-owned");
        }
    },

    RANK_NOT_OWNED() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("rank-not-owned");
        }
    },

    RANK_PERMISSION_NOT_OWNED() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("rank-permission-not-owned");
        }
    },

    COMMAND_COOLDOWN() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("command-cooldown");
        }
    },

    INVALID_COORDINATES() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("coordinates-invalid");
        }
    },

    HELPOP_SUBMITTED() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("helpop-submitted");
        }
    },

    HELPOP_REQUEST() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("helpop-message");
        }
    },

    LIST_MESSAGE() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("list-message");
        }
    },

    FROZEN_MESSAGE() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("frozen.message");
        }
    },

    COMMAND_NOT_ALLOWED_BANNED() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("ban.command-not-allowed");
        }
    },

    COMMAND_NOT_ALLOWED_MUTED() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("mute.command-not-allowed");
        }
    },

    CANT_USE_CHAT_MUTED() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("mute.cant-chat");
        }
    },

    CANT_USE_CHAT_BANNED() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("ban.cant-chat");
        }
    },

    KICK_MESSAGE() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("kick.message");
        }
    },

    KICK_ALERT() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("kick.alert");
        }
    },

    BAN_TEMPORARY() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("ban.temporary");
        }
    },

    BAN_PERMANENT() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("ban.permanent");
        }
    },

    BAN_PERMANENT_ALERT() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("ban.permanent-alert");
        }
    },

    BAN_TEMPORARY_ALERT() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("ban.temporary-alert");
        }
    },

    NOT_BANNED() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("ban.not-banned");
        }
    },

    BLACKLIST_TEMPORARY() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("blacklist.temporary");
        }
    },

    BLACKLIST_PERMANENT() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("blacklist.permanent");
        }
    },

    BLACKLIST_PERMANENT_ALERT() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("blacklist.permanent-alert");
        }
    },

    BLACKLIST_TEMPORARY_ALERT() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("blacklist.temporary-alert");
        }
    },

    MUTE_TEMPORARY() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("mute.temporary");
        }
    },

    MUTE_PERMANENT() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("mute.permanent");
        }
    },

    MUTE_PERMANENT_ALERT() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("mute.permanent-alert");
        }
    },

    MUTE_TEMPORARY_ALERT() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("mute.temporary-alert");
        }
    },

    NOT_MUTED() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("mute.not-muted");
        }
    },

    PARDON_MESSAGE() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("pardon-message");
        }
    },

    PUNISHMENT_HOVER() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("punishment-hover");
        }
    },

    PUNISHMENT_SILENT_PREFIX() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("punishment-silent-prefix");
        }
    },

    PUNISHMENT_MESSAGE() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("punishment-message");
        }
    },

    CONSOLE_NAME() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("console-name");
        }
    },

    HELP_CMD() {
        @Override
        public String toString() {
            return JavaPlugin.getPlugin(Basics.class).getConfigManager().getLanguageConfig().getString("help-command");
        }
    };

    @NotNull
    public static String getFancyName(@NotNull String name) {
        return name.toLowerCase().replace("_", " ");
    }

}
