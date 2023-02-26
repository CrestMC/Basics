package me.blurmit.basics.punishments;

import me.blurmit.basics.util.lang.Messages;

import java.util.HashMap;
import java.util.Map;

public enum PunishmentType {

    PERM_BLACKLIST() {
        @Override
        public Messages message() {
            return Messages.BLACKLIST_PERMANENT;
        }
    },

    TEMP_BLACKLIST() {
        @Override
        public Messages message() {
            return Messages.BLACKLIST_TEMPORARY;
        }
    },

    BLACKLIST() {
        @Override
        public Messages message() {
            return Messages.BLACKLIST_PERMANENT_ALERT;
        }

        @Override
        public String toString() {
            return "blacklists";
        }
    },

    UNBLACKLIST() {
        @Override
        public Messages message() {
            return Messages.PARDON_MESSAGE;
        }
    },

    PERM_BAN() {
        @Override
        public Messages message() {
            return Messages.BAN_PERMANENT;
        }
    },

    TEMP_BAN() {
        @Override
        public Messages message() {
            return Messages.BAN_TEMPORARY;
        }
    },

    BAN() {
        @Override
        public Messages message() {
            return Messages.BAN_PERMANENT_ALERT;
        }

        @Override
        public String toString() {
            return "bans";
        }
    },

    UNBAN() {
        @Override
        public Messages message() {
            return Messages.PARDON_MESSAGE;
        }
    },

    PERM_MUTE() {
        @Override
        public Messages message() {
            return Messages.MUTE_PERMANENT;
        }
    },

    TEMP_MUTE() {
        @Override
        public Messages message() {
            return Messages.MUTE_TEMPORARY;
        }
    },

    MUTE() {
        @Override
        public Messages message() {
            return Messages.MUTE_PERMANENT_ALERT;
        }

        @Override
        public String toString() {
            return "mutes";
        }
    },

    UNMUTE() {
        @Override
        public Messages message() {
            return Messages.PARDON_MESSAGE;
        }
    },

    KICK() {
        @Override
        public Messages message() {
            return Messages.KICK_MESSAGE;
        }

        @Override
        public String toString() {
            return "history";
        }
    },

    WARN() {
        @Override
        public Messages message() {
            return Messages.WARN_MESSAGE;
        }

        @Override
        public String toString() {
            return "warns";
        }
    };

    public abstract Messages message();

    private static final Map<String, PunishmentType> types = new HashMap<>();

    static {
        for (PunishmentType type : values()) {
            types.put(type.toString(), type);
        }
    }

    public static PunishmentType getByName(String name) {
        return types.get(name);
    }

}
