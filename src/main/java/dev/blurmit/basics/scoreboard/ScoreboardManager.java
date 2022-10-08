package dev.blurmit.basics.scoreboard;

import dev.blurmit.basics.Basics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;

public class ScoreboardManager implements Listener {

    public Map<Player, Scoreboards> boards;

    public ScoreboardManager(Basics plugin) {
        scheduleUpdate();

        this.boards = new HashMap<>();

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private void register(Player player) {
        Scoreboards scoreboards = new Scoreboards().getNew().show(player);
        boards.put(player, scoreboards);
    }

    private void unregister(Player player) {
        boards.get(player).getObjective().unregister();
        boards.remove(player);
    }

    private void scheduleUpdate() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Basics.getInstance(), () -> {
            boards.keySet().forEach(player -> boards.get(player).update(player));
        }, 20, 20 * 5);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        register(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        unregister(event.getPlayer());
    }

}
