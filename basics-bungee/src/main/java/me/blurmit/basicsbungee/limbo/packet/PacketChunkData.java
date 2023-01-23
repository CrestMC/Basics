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

import java.nio.charset.StandardCharsets;

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
            writeVarInt(chunk.getChunkMapPalette().length, buf);
            buf.writeBytes(chunk.getChunkMapPalette());
            return;
        }

        if (protocolVersion == ProtocolConstants.MINECRAFT_1_9_4) {
            buf.writeInt(0); // entity id
            buf.writeByte(0); // game mode
            buf.writeInt(0); // dimension
            buf.writeByte(0); // difficulty
            buf.writeByte(100); // max players
            buf.writeBytes("flat".getBytes(StandardCharsets.UTF_8)); // level type
            buf.writeBoolean(true); // debug info
            return;
        }

        buf.writeInt(1); // chunk x
        buf.writeInt(1); // chunk y
        buf.writeBoolean(true);
        DefinedPacket.writeVarInt(25565, buf);
        buf.writeBytes(new byte[] { 0, 0 }); // chunk map
        buf.writeBytes(new byte[0]);
    }

    @Override
    public void handle(AbstractPacketHandler handler) {
        // ... No?
    }

}
