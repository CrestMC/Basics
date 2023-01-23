package me.blurmit.basicsbungee.limbo;

import me.blurmit.basicsbungee.BasicsBungee;
import me.blurmit.basicsbungee.limbo.packet.PacketChunkData;
import me.blurmit.basicsbungee.limbo.packet.PacketPlayerPosition;
import me.blurmit.basicsbungee.limbo.protocol.ProtocolMapping;
import me.blurmit.basicsbungee.limbo.world.Chunk;
import me.blurmit.basicsbungee.limbo.world.Schematic;
import me.blurmit.basicsbungee.limbo.world.World;
import me.blurmit.basicsbungee.util.Messages;
import me.blurmit.basicsbungee.util.Protocols;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.score.Objective;
import net.md_5.bungee.api.score.Score;
import net.md_5.bungee.api.score.Team;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.*;
import se.llbit.nbt.*;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;

public class LimboManager {

    private final Set<UUID> limboPlayers;

    private final BasicsBungee plugin;
    private final LimboListener listener;

    private World world;

    public LimboManager(BasicsBungee plugin) {
        this.plugin = plugin;

        this.limboPlayers = new HashSet<>();
        this.listener = new LimboListener(plugin);

        Protocols.registerPacket(
                PacketPlayerPosition.class,
                PacketPlayerPosition::new,
                new ProtocolMapping(ProtocolConstants.MINECRAFT_1_8, 0x08),
                new ProtocolMapping(ProtocolConstants.MINECRAFT_1_9, 0x2E)
        );

        try (FileInputStream inputStream = new FileInputStream(plugin.getConfigManager().getConfig().getString("Limbo-Schematic-File"))) {
            world = Schematic.parseWorld((CompoundTag) CompoundTag.read(new DataInputStream(new GZIPInputStream(inputStream))));
        } catch (IOException e) {
            world = null;
            plugin.getLogger().log(Level.SEVERE, "An error occurred whilst attempting to read limbo schematic", e);
        }

        handleKeepAlive();
    }

    public void handleKeepAlive() {
        plugin.getProxy().getScheduler().schedule(plugin, () ->  {
            limboPlayers.forEach(uuid -> {
                ProxiedPlayer player = plugin.getProxy().getPlayer(uuid);

                if (player == null) {
                    return;
                }

                player.unsafe().sendPacket(new KeepAlive());
            });
        }, 10L, 10L, TimeUnit.SECONDS);
    }

    public void banishToLimbo(ProxiedPlayer player) {
        clearClientScoreboard(player);
        clearClientTabList(player);
        clearClientBossBar(player);

        sendRespawnPacket(player);
        sendPlayerPositionPacket(player);
        sendChunkDataPacket(player);

        player.sendMessage(Messages.LIMBO_SPAWN.text());
    }

    private void sendLoginPacket(ProxiedPlayer player) {
        plugin.getProxy().getScheduler().runAsync(plugin, () -> {
            Login login = new Login();
            login.setWorldName("limbo");
            login.setLevelType("flat");
            login.setEntityId(0);
            login.setHardcore(true);
            login.setGameMode((short) 1);
            login.setPreviousGameMode((short) 2);
            login.setWorldNames(new HashSet<>(Collections.singletonList("limbo")));

            int version = player.getPendingConnection().getVersion();

            if (version >= ProtocolConstants.MINECRAFT_1_16) {
                List<NamedTag> items = new ArrayList<>();
                items.add(new NamedTag("ambient_light", new ByteTag(1)));
                items.add(new NamedTag("infiniburn", new StringTag("0")));
                items.add(new NamedTag("logical_height", new IntTag(1)));
                items.add(new NamedTag("has_raids", new ByteTag(0)));
                items.add(new NamedTag("respawn_anchor_works", new ByteTag(1)));
                items.add(new NamedTag("bed_works", new ByteTag(1)));
                items.add(new NamedTag("piglin_safe", new ByteTag(0)));
                items.add(new NamedTag("coordinate_scale", new ByteTag(1)));
                items.add(new NamedTag("natural", new ByteTag(1)));
                items.add(new NamedTag("ultrawarm", new ByteTag(0)));
                items.add(new NamedTag("has_ceiling", new ByteTag(0)));
                items.add(new NamedTag("has_skylight", new ByteTag(1)));

                SpecificTag payload = new CompoundTag(items);
                login.setDimension(new NamedTag("dimension", payload));
            } else {
                login.setDimension(0);
            }

            login.setSeed(100);
            login.setDifficulty((short) 1);
            login.setMaxPlayers(1);
            login.setViewDistance(10);
            login.setReducedDebugInfo(true);
            login.setNormalRespawn(false);
            login.setDebug(true);
            login.setFlat(true);

            player.unsafe().sendPacket(login);
        });
    }

    private void sendRespawnPacket(ProxiedPlayer player) {
        plugin.getProxy().getScheduler().runAsync(plugin, () -> {
            Respawn respawn = new Respawn();
            respawn.setWorldName("limbo");
            respawn.setLevelType("flat");
            respawn.setGameMode((short) 1);
            respawn.setPreviousGameMode((short) 2);

            int version = player.getPendingConnection().getVersion();

            if (version >= ProtocolConstants.MINECRAFT_1_16) {
                List<NamedTag> items = new ArrayList<>();
                items.add(new NamedTag("ambient_light", new ByteTag(1)));
                items.add(new NamedTag("infiniburn", new StringTag("0")));
                items.add(new NamedTag("logical_height", new IntTag(1)));
                items.add(new NamedTag("has_raids", new ByteTag(0)));
                items.add(new NamedTag("respawn_anchor_works", new ByteTag(1)));
                items.add(new NamedTag("bed_works", new ByteTag(1)));
                items.add(new NamedTag("piglin_safe", new ByteTag(0)));
                items.add(new NamedTag("coordinate_scale", new ByteTag(1)));
                items.add(new NamedTag("natural", new ByteTag(1)));
                items.add(new NamedTag("ultrawarm", new ByteTag(0)));
                items.add(new NamedTag("has_ceiling", new ByteTag(0)));
                items.add(new NamedTag("has_skylight", new ByteTag(1)));

                SpecificTag payload = new CompoundTag(items);
                respawn.setDimension(new NamedTag("dimension", payload));
            } else {
                respawn.setDimension(0);
            }

            respawn.setSeed(100);
            respawn.setDifficulty((short) 1);
            respawn.setDebug(true);
            respawn.setFlat(true);

            player.unsafe().sendPacket(respawn);
        });
    }

    private void sendChunkDataPacket(ProxiedPlayer player) {
        plugin.getLogger().info("Sending Limbo chunk map to " + player.getName() + "...");

//        Protocols.registerPacket(
//                PacketChunkData.class,
//                PacketChunkData::new,
//                new ProtocolMapping(ProtocolConstants.MINECRAFT_1_8, 0x21),
//                new ProtocolMapping(ProtocolConstants.MINECRAFT_1_9, 0x20),
//                new ProtocolMapping(ProtocolConstants.MINECRAFT_1_9_1, 0x23),
//                new ProtocolMapping(ProtocolConstants.MINECRAFT_1_9_4, 0x20)
//        );

        plugin.getProxy().getScheduler().runAsync(plugin, () -> {
            for (Chunk[] tab : BasicsBungee.getInstance().getLimboManager().getWorld().getChunks()) {
                for (Chunk chunk : tab) {
                    if (chunk != null) {
                        player.unsafe().sendPacket(new PacketChunkData(chunk));
                    }
                }
            }
        });
    }

    private void sendPlayerPositionPacket(ProxiedPlayer player) {
        plugin.getProxy().getScheduler().runAsync(plugin, () -> {
            PacketPlayerPosition playerPosition = new PacketPlayerPosition();
            playerPosition.setX(plugin.getConfigManager().getConfig().getDouble("Limbo-Spawn-X"));
            playerPosition.setY(plugin.getConfigManager().getConfig().getDouble("Limbo-Spawn-Y"));
            playerPosition.setZ(plugin.getConfigManager().getConfig().getDouble("Limbo-Spawn-Z"));
            playerPosition.setYaw(plugin.getConfigManager().getConfig().getFloat("Limbo-Spawn-Yaw"));
            playerPosition.setPitch(plugin.getConfigManager().getConfig().getFloat("Limbo-Spawn-Pitch"));

             player.unsafe().sendPacket(playerPosition);
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

    public Set<UUID> getLimboPlayers() {
        return limboPlayers;
    }

    public World getWorld() {
        return world;
    }

}
