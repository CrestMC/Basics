package me.blurmit.basics.util;

import org.jetbrains.annotations.NotNull;

public class Booleans {

    public static boolean isBoolean(@NotNull String bool) {
        try {
            return Boolean.parseBoolean(bool);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean isFancyBoolean(@NotNull String bool) {
        switch (bool.toLowerCase()) {
            case "true":
            case "on":
            case "yes":
            case "enabled":
            case "enable":
            case "1":
                return true;
            default:
                return false;
        }
    }

    public static String getFancyBoolean(boolean bool) {
        return bool ? "enabled" : "disabled";
    }
}
