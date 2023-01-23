package me.blurmit.basicsbungee.util;

import com.google.common.base.Preconditions;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import me.blurmit.basicsbungee.limbo.protocol.ProtocolMapping;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.ProtocolConstants;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.logging.Level;

public class Protocols {

    @SuppressWarnings("unchecked")
    public static void registerPacket(Class<? extends DefinedPacket> packetClass, Supplier<? extends DefinedPacket> constructor, ProtocolMapping... mappings) {
        try {
            int mappingIndex = 0;
            ProtocolMapping mapping = mappings[mappingIndex];

            for (int protocol : ProtocolConstants.SUPPORTED_VERSION_IDS) {
                if (protocol < mapping.getProtocolVersion()) {
                    // This is a new packet, skip it till we reach the next protocol
                    continue;
                }

                if (mapping.getProtocolVersion() < protocol && mappingIndex + 1 < mappings.length) {
                    // Mapping is non current, but the next one may be ok
                    ProtocolMapping nextMapping = mappings[mappingIndex + 1];

                    if (nextMapping.getProtocolVersion() == protocol) {
                        Preconditions.checkState(nextMapping.getPacketID() != mapping.getPacketID(), "Duplicate packet mapping (%s, %s)", mapping.getProtocolVersion(), nextMapping.getProtocolVersion());

                        mapping = nextMapping;
                        mappingIndex++;
                    }
                }

                if (mapping.getPacketID() < 0) {
                    break;
                }

                Field toClientDirectionDataField = Class.forName("net.md_5.bungee.protocol.Protocol").getDeclaredField("TO_CLIENT");
                toClientDirectionDataField.setAccessible(true);
                Object toClientDirectionData = toClientDirectionDataField.get(Protocol.GAME);

                Field protocolsField = Arrays.stream(Protocol.class.getDeclaredClasses())
                        .filter(clazz -> clazz.getName().contains("DirectionData"))
                        .findFirst()
                        .get()
                        .getDeclaredField("protocols");
                protocolsField.setAccessible(true);
                TIntObjectMap<Object> protocols = (TIntObjectMap<Object>) protocolsField.get(toClientDirectionData);

                Object data = protocols.get(protocol);

                Field packetMapField = data.getClass().getDeclaredField("packetMap");
                packetMapField.setAccessible(true);
                TObjectIntMap<Class<? extends DefinedPacket>> packetMap = (TObjectIntMap<Class<? extends DefinedPacket>>) packetMapField.get(data);

                Field packetConstructorsField = data.getClass().getDeclaredField("packetConstructors");
                packetConstructorsField.setAccessible(true);
                Supplier<? extends DefinedPacket>[] packetConstructors = (Supplier<? extends DefinedPacket>[]) packetConstructorsField.get(data);

                packetMap.put(packetClass, mapping.getPacketID());
                packetConstructors[mapping.getPacketID()] = constructor;
            }
        } catch (ReflectiveOperationException e) {
            ProxyServer.getInstance().getLogger().log(Level.SEVERE, "An error occurred whilst attempting to register a packet", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static void unregisterPacket(Class<? extends DefinedPacket> packetClass, Supplier<? extends DefinedPacket> constructor, ProtocolMapping... mappings) {
        try {
            int mappingIndex = 0;
            ProtocolMapping mapping = mappings[mappingIndex];

            for (int protocol : ProtocolConstants.SUPPORTED_VERSION_IDS) {
                if (protocol < mapping.getProtocolVersion()) {
                    // This is a new packet, skip it till we reach the next protocol
                    continue;
                }

                if (mapping.getProtocolVersion() < protocol && mappingIndex + 1 < mappings.length) {
                    // Mapping is non current, but the next one may be ok
                    ProtocolMapping nextMapping = mappings[mappingIndex + 1];

                    if (nextMapping.getProtocolVersion() == protocol) {
                        Preconditions.checkState(nextMapping.getPacketID() != mapping.getPacketID(), "Duplicate packet mapping (%s, %s)", mapping.getProtocolVersion(), nextMapping.getProtocolVersion());

                        mapping = nextMapping;
                        mappingIndex++;
                    }
                }

                if (mapping.getPacketID() < 0) {
                    break;
                }

                Field toClientDirectionDataField = Class.forName("net.md_5.bungee.protocol.Protocol").getDeclaredField("TO_CLIENT");
                toClientDirectionDataField.setAccessible(true);
                Object toClientDirectionData = toClientDirectionDataField.get(Protocol.GAME);

                Field protocolsField = Arrays.stream(Protocol.class.getDeclaredClasses())
                        .filter(clazz -> clazz.getName().contains("DirectionData"))
                        .findFirst()
                        .get()
                        .getDeclaredField("protocols");
                protocolsField.setAccessible(true);
                TIntObjectMap<Object> protocols = (TIntObjectMap<Object>) protocolsField.get(toClientDirectionData);

                Object data = protocols.get(protocol);

                Field packetMapField = data.getClass().getDeclaredField("packetMap");
                packetMapField.setAccessible(true);
                TObjectIntMap<Class<? extends DefinedPacket>> packetMap = (TObjectIntMap<Class<? extends DefinedPacket>>) packetMapField.get(data);

                Field packetConstructorsField = data.getClass().getDeclaredField("packetConstructors");
                packetConstructorsField.setAccessible(true);
                Supplier<? extends DefinedPacket>[] packetConstructors = (Supplier<? extends DefinedPacket>[]) packetConstructorsField.get(data);

                packetMap.remove(packetClass);
                packetConstructors[mapping.getPacketID()] = null;
            }
        } catch (ReflectiveOperationException e) {
            ProxyServer.getInstance().getLogger().log(Level.SEVERE, "An error occurred whilst attempting to register a packet", e);
        }
    }

}
