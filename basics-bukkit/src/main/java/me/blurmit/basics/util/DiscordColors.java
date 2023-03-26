package me.blurmit.basics.util;

import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;

public enum DiscordColors {

    BLACK('0', 30) {
        @Override
        public ChatColor asBukkit() {
            return ChatColor.BLACK;
        }
    },
    DARK_BLUE('1', 36) {
        @Override
        public ChatColor asBukkit() {
            return ChatColor.DARK_BLUE;
        }
    },
    DARK_GREEN('2', 32) {
        @Override
        public ChatColor asBukkit() {
            return ChatColor.DARK_GREEN;
        }
    },
    DARK_AQUA('3', 36) {
        @Override
        public ChatColor asBukkit() {
            return ChatColor.DARK_AQUA;
        }
    },
    DARK_RED('4', 31) {
        @Override
        public ChatColor asBukkit() {
            return ChatColor.DARK_RED;
        }
    },
    DARK_PURPLE('5', 35) {
        @Override
        public ChatColor asBukkit() {
            return ChatColor.DARK_PURPLE;
        }
    },
    GOLD('6', 33) {
        @Override
        public ChatColor asBukkit() {
            return ChatColor.GOLD;
        }
    },
    GRAY('7', 0) {
        @Override
        public ChatColor asBukkit() {
            return ChatColor.GRAY;
        }
    },
    DARK_GRAY('8', 30) {
        @Override
        public ChatColor asBukkit() {
            return ChatColor.DARK_GRAY;
        }
    },
    BLUE('9', 34) {
        @Override
        public ChatColor asBukkit() {
            return ChatColor.BLUE;
        }
    },
    GREEN('a', 32) {
        @Override
        public ChatColor asBukkit() {
            return ChatColor.GREEN;
        }
    },
    AQUA('b', 34) {
        @Override
        public ChatColor asBukkit() {
            return ChatColor.AQUA;
        }
    },
    RED('c', 31) {
        @Override
        public ChatColor asBukkit() {
            return ChatColor.RED;
        }
    },
    LIGHT_PURPLE('d', 35) {
        @Override
        public ChatColor asBukkit() {
            return ChatColor.LIGHT_PURPLE;
        }
    },
    YELLOW('e', 33) {
        @Override
        public ChatColor asBukkit() {
            return ChatColor.YELLOW;
        }
    },
    WHITE('f', 0) {
        @Override
        public ChatColor asBukkit() {
            return ChatColor.WHITE;
        }
    },
    MAGIC('k', 42, true) {
        @Override
        public ChatColor asBukkit() {
            return ChatColor.MAGIC;
        }
    },
    BOLD('l', 1, true) {
        @Override
        public ChatColor asBukkit() {
            return ChatColor.BOLD;
        }
    },
    STRIKETHROUGH('m', 4, true) {
        @Override
        public ChatColor asBukkit() {
            return ChatColor.STRIKETHROUGH;
        }
    },
    UNDERLINE('n', 4, true) {
        @Override
        public ChatColor asBukkit() {
            return ChatColor.UNDERLINE;
        }
    },
    ITALIC('o', 7, true) {
        @Override
        public ChatColor asBukkit() {
            return ChatColor.ITALIC;
        }
    },
    RESET('r', 0, false) {
        @Override
        public ChatColor asBukkit() {
            return ChatColor.RESET;
        }
    };

    public static final String COLOR_CHARS_START = "[";
    public static final char COLOR_CHARS_END = 'm';

    private final int intCode;
    private final char code;
    private final boolean isFormat;
    private final String toString;

    private static final Map<Integer, DiscordColors> BY_ID = new HashMap<>();
    private static final Map<Character, DiscordColors> BY_CHAR = new HashMap<>();


    DiscordColors(char code, int intCode) {
        this(code, intCode, false);
    }

    DiscordColors(char code, int intCode, boolean isFormat) {
        this.code = code;
        this.intCode = intCode;
        this.toString = COLOR_CHARS_START + intCode + COLOR_CHARS_END;
        this.isFormat = isFormat;
    }

    public ChatColor asBukkit() {
        return ChatColor.RESET;
    }

    public char getChar() {
        return code;
    }

    public int getIntCode() {
        return intCode;
    }

    public boolean isFormat() {
        return isFormat;
    }

    @Override
    public String toString() {
        return toString;
    }

    public static DiscordColors getByChar(char code) {
        return BY_CHAR.get(code);
    }

    public static DiscordColors getByChar(String code) {
        if (code.length() > 1) {
            return DiscordColors.WHITE;
        }

        return BY_CHAR.get(code.charAt(0));
    }

    public static DiscordColors getByID(int code) {
        return BY_ID.get(code);
    }

    public static String translate(char altColorChar, String text) {
        if (text == null) {
            return null;
        }

        char[] textCharArray = text.toCharArray();
        String[] textArray = new String[textCharArray.length];
        for (int i = 0; i < textArray.length; i++) {
            textArray[i] = textCharArray[i] + "";
        }

        DiscordColors previousColor = null;
        for (int i = 0; i < textArray.length - 1; i++) {
            DiscordColors color = getByChar(textArray[i + 1]);
            if (textArray[i].equals(altColorChar + "") && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".contains(textArray[i + 1]) && color != null) {
                if (color.isFormat() && previousColor != null) {
                    try {
                        Integer.parseInt(textArray[i - 2].replaceFirst("\\[", ""));
                    } catch (NumberFormatException e) {
                        continue;
                    }

                    String prefixAndID = textArray[i - 2];
                    String suffix = textArray[i - 1];

                    textArray[i - 2] = DiscordColors.COLOR_CHARS_START + color.getIntCode();
                    textArray[i - 1] = DiscordColors.COLOR_CHARS_END + "";

                    textArray[i] = prefixAndID;
                    textArray[i + 1] = suffix;

                    previousColor = color;
                    continue;
                }

                textArray[i] = DiscordColors.COLOR_CHARS_START + color.getIntCode();
                textArray[i + 1] = DiscordColors.COLOR_CHARS_END + "";
                previousColor = color;
            }
        }

        StringBuilder translatedText = new StringBuilder();
        for (String arrayText : textArray) {
            translatedText.append(arrayText);
        }

        return translatedText.toString();
    }

    static {
        for (DiscordColors color : values()) {
            BY_ID.put(color.intCode, color);
            BY_CHAR.put(color.code, color);
        }
    }

}
