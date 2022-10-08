package dev.blurmit.basics.util;

import org.bukkit.GameMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Gamemodes {

    @Nullable
    public static GameMode getGamemode(@NotNull String gamemode) {
        switch (gamemode.toLowerCase()) {
            case "creative":
            case "c":
            case "1":
                return GameMode.CREATIVE;
            case "survival":
            case "s":
            case "0":
                return GameMode.SURVIVAL;
            case "adventure":
            case "a":
            case "2":
                return GameMode.ADVENTURE;
            case "spectator":
            case "sp":
            case "3":
                return GameMode.SPECTATOR;
            default:
                return null;
        }
    }

}
