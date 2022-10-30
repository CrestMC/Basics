package me.blurmit.basicsbungee.util.lang;

import me.blurmit.basicsbungee.BasicsBungee;
import me.blurmit.basicsbungee.util.Placeholders;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;

public enum Messages {

    ONLY_PLAYERS() {
        @Override
        public TextComponent text() {
            return new TextComponent(Placeholders.parsePlaceholder(BasicsBungee.getConfigManager().getLanguageConfig().getString("only-players")));
        }
    },

    BROKEN_SERVER() {
        @Override
        public TextComponent text() {
            return new TextComponent(Placeholders.parsePlaceholder(BasicsBungee.getConfigManager().getLanguageConfig().getString("broken-server")));
        }
    },

    RESTRICTED_SERVER() {
        @Override
        public TextComponent text() {
            return new TextComponent(Placeholders.parsePlaceholder(ProxyServer.getInstance().getTranslation("no_server_permission")));
        }
    };

    public abstract TextComponent text();

}
