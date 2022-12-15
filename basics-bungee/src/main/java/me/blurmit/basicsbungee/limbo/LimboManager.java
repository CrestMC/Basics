package me.blurmit.basicsbungee.limbo;

import me.blurmit.basicsbungee.BasicsBungee;
import me.blurmit.basicsbungee.util.lang.Messages;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.api.score.Objective;
import net.md_5.bungee.api.score.Score;
import net.md_5.bungee.api.score.Team;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.logging.Level;

public class LimboManager {

    private final Map<UUID, ScheduledTask> keepAliveTasks;

    private final BasicsBungee plugin;
    private final LimboListener listener;

    public LimboManager(BasicsBungee plugin) {
        this.plugin = plugin;

        this.keepAliveTasks = new HashMap<>();
        this.listener = new LimboListener(plugin);
    }

    public void handleKeepAlive(ProxiedPlayer player) {
        ScheduledTask task = plugin.getProxy().getScheduler().schedule(plugin, () -> player.unsafe().sendPacket(new KeepAlive()), 10L, 10L, TimeUnit.SECONDS);
        getKeepAliveTasks().put(player.getUniqueId(), task);
    }

    public void banishToLimbo(ProxiedPlayer player) {
        sendLoginPacket(player);

        clearClientScoreboard(player);
        clearClientTabList(player);
        clearClientBossBar(player);

        sendRespawnPacket(player);
        handleKeepAlive(player);

        player.sendMessage(Messages.LIMBO_SPAWN.text());
    }

    public Map<UUID, ScheduledTask> getKeepAliveTasks() {
        return keepAliveTasks;
    }

    private void sendLoginPacket(ProxiedPlayer player) {
        plugin.getProxy().getScheduler().runAsync(plugin, () -> {
            Login login = new Login();
            login.setWorldName("Limbo");
            login.setLevelType("flat");
            login.setEntityId(1);
            login.setHardcore(true);
            login.setGameMode((short) 3);
            login.setPreviousGameMode((short) 2);
            login.setWorldNames(new HashSet<>(Collections.singletonList("Limbo")));
            login.setDimension(3);
            login.setSeed(100);
            login.setDifficulty((short) 1);
            login.setMaxPlayers(1);
            login.setViewDistance(10);
            login.setReducedDebugInfo(true);
            login.setNormalRespawn(true);
            login.setDebug(true);
            login.setFlat(true);

            player.unsafe().sendPacket(login);
        });
    }

    private void sendRespawnPacket(ProxiedPlayer player) {
        plugin.getProxy().getScheduler().runAsync(plugin, () -> {
            Respawn respawn = new Respawn();
            respawn.setWorldName("Limbo");
            respawn.setLevelType("flat");
            respawn.setGameMode((short) 3);
            respawn.setPreviousGameMode((short) 2);
            respawn.setDimension(3);
            respawn.setSeed(100);
            respawn.setDifficulty((short) 1);
            respawn.setDebug(true);
            respawn.setFlat(true);

            player.unsafe().sendPacket(respawn);
        });
    }

    private void clearClientScoreboard(ProxiedPlayer player) {
        try {
            Field scoreboardField = Class.forName("net.md_5.bungee.UserConnection").getDeclaredField("serverSentScoreboard");
            scoreboardField.setAccessible(true);
            Object scoreboard = scoreboardField.get(player);

            for (Objective objective : (Collection<Objective>) scoreboard.getClass().getMethod("getObjectives").invoke(scoreboard)) {
                player.unsafe().sendPacket(new ScoreboardObjective(objective.getName(), objective.getValue(), ScoreboardObjective.HealthDisplay.fromString(objective.getType()), (byte) 1));
            }

            for (Score score : (Collection<Score>) scoreboard.getClass().getMethod("getScores").invoke(scoreboard)) {
                player.unsafe().sendPacket(new ScoreboardScore(score.getItemName(), (byte) 1, score.getScoreName(), score.getValue()));
            }

            for (Team team : (Collection<Team>) scoreboard.getClass().getMethod("getTeams").invoke(scoreboard)) {
                player.unsafe().sendPacket(new net.md_5.bungee.protocol.packet.Team(team.getName()));
            }

            Method scoreboardClearMethod = scoreboard.getClass().getMethod("clear");
            scoreboardClearMethod.invoke(scoreboard);
        } catch (ReflectiveOperationException e) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred whilst attempting to clear client data of " + player.getName(), e);
        }
    }

    private void clearClientBossBar(ProxiedPlayer player) {
        try {
            Field sentBossBarsField = Class.forName("net.md_5.bungee.UserConnection").getDeclaredField("sentBossBars");
            sentBossBarsField.setAccessible(true);

            Object sentBossBars = sentBossBarsField.get(player);

            for (UUID bossbar : (Collection<UUID>) sentBossBars) {
                player.unsafe().sendPacket(new BossBar(bossbar, 1));
            }

            Method bossBarsClearMethod = sentBossBars.getClass().getDeclaredMethod("clear");
            bossBarsClearMethod.invoke(sentBossBars);
        } catch (ReflectiveOperationException e) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred whilst attempting to clear the boss bar of " + player.getName(), e);
        }
    }

    private void clearClientTabList(ProxiedPlayer player) {
        try {
            Field tablistField = Class.forName("net.md_5.bungee.UserConnection").getDeclaredField("tabListHandler");
            tablistField.setAccessible(true);
            Object tablist = tablistField.get(player);

            Field uuidsCollectionField = tablist.getClass().getDeclaredField("uuids");
            uuidsCollectionField.setAccessible(true);
            Collection<UUID> uuidsCollection = (Collection<UUID>) uuidsCollectionField.get(tablist);

            PlayerListItem packet = new PlayerListItem();
            packet.setAction(PlayerListItem.Action.REMOVE_PLAYER);

            PlayerListItem.Item[] items = new PlayerListItem.Item[uuidsCollection.size()];
            int i = 0;

            for (UUID uuid : uuidsCollection) {
                PlayerListItem.Item item = items[i++] = new PlayerListItem.Item();
                item.setUuid(uuid);

                if (uuid.equals(player.getUniqueId())) {
                    item.setUuid(UUID.randomUUID());
                }
            }

            packet.setItems(items);
            player.unsafe().sendPacket(packet);
            uuidsCollection.clear();
        } catch (ReflectiveOperationException e) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred whilst attempting to clear the tab list of " + player.getName(), e);
        }
    }

}
