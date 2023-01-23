package me.blurmit.basics.command.defined;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import me.blurmit.basics.Basics;
import me.blurmit.basics.command.CommandBase;
import me.blurmit.basics.util.Placeholders;
import me.blurmit.basics.util.ReflectionUtil;
import me.blurmit.basics.util.UUIDs;
import me.blurmit.basics.util.lang.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class SkinCommand extends CommandBase {

    private final Basics plugin;
    private final Map<String, Object> texturePropertyMap;

    public SkinCommand(Basics plugin) {
        super(plugin.getName());
        setName("skin");
        setDescription("Changes the skin of a player");
        setCooldown(3);
        setUsage("/skin <skin>");
        setPermission("basics.command.skin");

        this.plugin = plugin;
        this.texturePropertyMap = new HashMap<>();

        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, texturePropertyMap::clear, 0L, TimeUnit.MINUTES.toMillis(10));
    }

    @SneakyThrows
    @Override
    public boolean execute(CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.NO_PERMISSION + "", sender, this, args));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.ONLY_PLAYERS + "", sender, this, args));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(Placeholders.parsePlaceholder(Messages.INVALID_ARGS + "", sender, this, args));
            return true;
        }

        Player player = (Player) sender;

        Object craftPlayer = player.getClass().getMethod("getHandle").invoke(player);
        Object connection = craftPlayer.getClass().getField("playerConnection").get(craftPlayer);
        Object profile = craftPlayer.getClass().getMethod("getProfile").invoke(craftPlayer);

        // TODO: 1.8 fix unloaded chunks
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            UUID uuid = UUIDs.synchronouslyRetrieveUUID(args[0]);

            if (uuid == null) {
                sender.sendMessage(Placeholders.parsePlaceholder(Messages.ACCOUNT_DOESNT_EXIST + "", true, args[0]));
                return;
            }

            sendPlayerInfoPacket(craftPlayer, connection, "REMOVE_PLAYER");
            reloadTexturesProperty(profile, args[0]);
            sendPlayerInfoPacket(craftPlayer, connection, "ADD_PLAYER");
            refreshPlayer(player, craftPlayer, connection);

            player.sendMessage(Placeholders.parsePlaceholder(Messages.SKIN_SET + "", true, UUIDs.synchronouslyGetNameFromUUID(uuid)));
        });
        return true;
    }

    @SneakyThrows
    private void reloadTexturesProperty(Object profile, String skin) {
        Object properties = profile.getClass().getMethod("getProperties").invoke(profile);
        properties.getClass().getMethod("removeAll", Object.class).invoke(properties, "textures");
        properties.getClass().getMethod("put", Object.class, Object.class).invoke(properties, "textures", getSkinTextureProperty(skin));
    }

    private void refreshPlayer(Player player, Object craftPlayer, Object connection) {
        player.getServer().getScheduler().runTask(plugin, () -> {
            plugin.getServer().getOnlinePlayers().forEach(onlinePlayer -> {
                onlinePlayer.hidePlayer(plugin, player);
                onlinePlayer.showPlayer(plugin, player);
            });
        });

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            sendRespawnPacket(player, craftPlayer, connection);

            player.setOp(player.isOp());

            player.setHealth(player.getHealth());
            player.setFoodLevel(player.getFoodLevel());
            player.setExp(player.getExp());
            player.setPlayerTime(player.getPlayerTime(), false);

            player.getInventory().setHeldItemSlot(player.getInventory().getHeldItemSlot());
            player.getInventory().setContents(player.getInventory().getContents());
            player.getInventory().setArmorContents(player.getInventory().getArmorContents());

            player.setAllowFlight(player.getAllowFlight());
            player.setFlying(player.isFlying());
            player.setInvisible(player.isInvisible());

            Collection<PotionEffect> activeEffects = player.getActivePotionEffects();

            for (PotionEffect effect : activeEffects) {
                player.removePotionEffect(effect.getType());
            }

            for (PotionEffect effect : activeEffects) {
                player.addPotionEffect(effect);
            }

            player.loadData();
        });
    }

    @SneakyThrows
    private void sendPlayerInfoPacket(Object craftPlayer, Object connection, String action) {
        Class<?> entityPlayer = ReflectionUtil.getNMSClass("EntityPlayer");

        Class<?> packetClass = ReflectionUtil.getNMSClass("Packet");
        Class<?> packetPlayOutPlayerInfoClass = ReflectionUtil.getNMSClass("PacketPlayOutPlayerInfo");
        Class enumPlayerInfoActionClass = Arrays.stream(packetPlayOutPlayerInfoClass.getClasses())
                .filter(Class::isEnum)
                .findFirst()
                .orElse(null);

        Enum<?> enumPlayerInfoAction = Enum.valueOf(enumPlayerInfoActionClass, action);

        Object entityPlayerArray = Array.newInstance(entityPlayer, 1);
        Array.set(entityPlayerArray, 0, craftPlayer);

        Object packetPlayOutPlayerInfo = Arrays.stream(packetPlayOutPlayerInfoClass.getConstructors())
                .filter(Constructor::isVarArgs)
                .findFirst()
                .orElse(null)
                .newInstance(enumPlayerInfoAction, entityPlayerArray);

        connection.getClass().getMethod("sendPacket", packetClass).invoke(connection, packetPlayOutPlayerInfo);
    }

    @SneakyThrows
    private void sendRespawnPacket(Player player, Object craftPlayer, Object connection) {
        Class<?> packetClass = ReflectionUtil.getNMSClass("Packet");
        Class<?> packetPlayOutRespawnClass = ReflectionUtil.getNMSClass("PacketPlayOutRespawn");

        Object worldClass = craftPlayer.getClass().getMethod("getWorld").invoke(craftPlayer);
        Object manager = worldClass.getClass().getMethod("getDimensionManager").invoke(worldClass);
        Object key = worldClass.getClass().getMethod("getDimensionKey").invoke(worldClass);

        Object playerInteractManager = craftPlayer.getClass().getField("playerInteractManager").get(craftPlayer);
        Object gamemode = playerInteractManager.getClass().getMethod("getGameMode").invoke(playerInteractManager);

        Object packetPlayOutRespawn = packetPlayOutRespawnClass
                .getConstructor(manager.getClass(), key.getClass(), long.class, gamemode.getClass(), gamemode.getClass(), boolean.class, boolean.class, boolean.class)
                .newInstance(manager, key, player.getWorld().getSeed(), gamemode, gamemode, true, true, true);

        connection.getClass().getMethod("sendPacket", packetClass).invoke(connection, packetPlayOutRespawn);
    }

    private Object getSkinTextureProperty(String name) {
        if (!texturePropertyMap.containsKey(name)) {
            try {
                UUID uuid = UUIDs.synchronouslyRetrieveUUID(name);

                URL sessionServer = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
                InputStreamReader sessionServerReader = new InputStreamReader(sessionServer.openStream());
                JsonObject textureProperty = new JsonParser().parse(sessionServerReader).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();

                String texture = textureProperty.get("value").getAsString();
                String signature = textureProperty.get("signature").getAsString();

                Class<?> propertyClass = Class.forName("com.mojang.authlib.properties.Property");
                texturePropertyMap.put(name, propertyClass.getConstructor(String.class, String.class, String.class).newInstance("textures", texture, signature));
            } catch (ReflectiveOperationException | IOException e) {
                return null;
            }
        }

        return texturePropertyMap.get(name);
    }

}
