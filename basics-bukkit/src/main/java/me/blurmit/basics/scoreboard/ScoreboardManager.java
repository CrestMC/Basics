package me.blurmit.basics.scoreboard;

import me.blurmit.basics.Basics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ScoreboardManager implements Listener {

    private final Map<Player, Scoreboards> boards;
    private final Basics plugin;

    public ScoreboardManager(Basics plugin) {
        this.plugin = plugin;
        this.boards = new HashMap<>();

        if (!plugin.getConfigManager().getConfig().getBoolean("Scoreboard-Enabled")) {
            return;
        }

        scheduleUpdate();

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void register(Player player) {
        Scoreboards scoreboards = new Scoreboards(plugin).getNew().show(player);
        boards.put(player, scoreboards);
    }

    public void unregister(Player player) {
        boards.get(player).getObjective().unregister();
        boards.remove(player);
    }

    public Scoreboards getScoreboard(Player player) {
        return boards.get(player);
    }

    public Collection<Scoreboards> getBoards() {
        return boards.values();
    }

    private void scheduleUpdate() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            boards.keySet().forEach(player -> boards.get(player).update(player));
        }, 0, 20L);
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