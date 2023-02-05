package me.blurmit.basicsbungee.limbo.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.blurmit.basicsbungee.limbo.world.Chunk;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PacketChunkData extends DefinedPacket {

    private Chunk chunk;

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        if (protocolVersion == ProtocolConstants.MINECRAFT_1_8) {
            buf.writeInt(chunk.getChunkX()); // Chunk X
            buf.writeInt(chunk.getChunkZ()); // Chunk Z
            buf.writeBoolean(true); // Reduced Debug Info
            buf.writeShort((short) 65535);
            writeVarInt(chunk.getChunkMap().length, buf);
            buf.writeBytes(chunk.getChunkMap());
            return;
        }

        if (protocolVersion == ProtocolConstants.MINECRAFT_1_9_1) {
            buf.writeInt(chunk.getChunkX()); // Chunk X
            buf.writeInt(chunk.getChunkZ()); // Chunk Z
            buf.writeBoolean(true); // Reduced Debug Info
            writeVarInt(25565, buf);
            writeVarInt(chunk.getChunkMap().length, buf);
            buf.writeBytes(chunk.getChunkMap());
            return;
        }

        if (protocolVersion >= ProtocolConstants.MINECRAFT_1_9_4) {
            buf.writeInt(chunk.getChunkX());
            buf.writeInt(chunk.getChunkZ());
            buf.writeBoolean(true);
            DefinedPacket.writeVarInt(chunk.getChunkMap().length, buf);
            buf.writeBytes(chunk.getChunkMap());
            buf.writeBytes(new byte[0]);
            return;
        }
//
//        buf.writeInt(chunk.getChunkX());
//        buf.writeInt(chunk.getChunkZ());
//        buf.writeBytes(new NamedTag("MOTION_BLOCKING", new CompoundTag(Collections.emptyList())).byteArray());
//        DefinedPacket.writeVarInt(chunk.getChunkMap().length, buf);
//        buf.writeBytes(chunk.getChunkMap());
//        DefinedPacket.writeVarInt(0, buf);
//        buf.writeBytes(new byte[0]);
//        buf.writeBoolean(false);
//        buf.writeBytes(new byte[0]);
//        buf.writeBytes(new byte[0]);
//        buf.writeBytes(new byte[0]);
//        buf.writeBytes(new byte[0]);
//        DefinedPacket.writeVarInt(0, buf);
//        buf.writeBytes(new byte[2048]);
//        DefinedPacket.writeVarInt(0, buf);
//        buf.writeBytes(new byte[2048]);
    }

    @Override
    public void handle(AbstractPacketHandler handler) {
        // ... No?
    }

}
