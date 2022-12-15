package me.blurmit.basics.punishments;

import me.blurmit.basics.util.lang.Messages;

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
    };

    public abstract Messages message();

}
