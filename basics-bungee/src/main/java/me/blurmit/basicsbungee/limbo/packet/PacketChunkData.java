package me.blurmit.basicsbungee.limbo.packet;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

import java.nio.charset.StandardCharsets;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PacketChunkData extends DefinedPacket {

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        ProxyServer.getInstance().getLogger().info("If you ever see this message, run. Hide your children. The apocalypse is here.");
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        if (protocolVersion < ProtocolConstants.MINECRAFT_1_8) {
            buf.writeInt(0);
            buf.writeByte(0);
            buf.writeByte(0);
            buf.writeByte(0);
            buf.writeByte(100);
            buf.readBytes("flat".getBytes(StandardCharsets.UTF_8));
            return;
        }

        if (protocolVersion < ProtocolConstants.MINECRAFT_1_9_1) {
            buf.writeInt(0);
            buf.writeInt(0);
            buf.writeBoolean(true);
            buf.writeShort((short) 65535);
            buf.writeBytes(new byte[0]);
            return;
        }

        if (protocolVersion < ProtocolConstants.MINECRAFT_1_9_4) {
            buf.writeInt(0); // entity id
            buf.writeInt(0); // game mode
            buf.writeInt(0); // dimension
            buf.writeByte(0); // difficulty
            buf.writeByte(100); // max players
            buf.writeBytes("flat".getBytes(StandardCharsets.UTF_8)); // level type
            return;
        }

        buf.writeInt(0); // chunk x
        buf.writeInt(0); // chunk y
        buf.writeBoolean(true);
        buf.writeInt(0);
        buf.writeBytes(new byte[0]); // chunk map
        buf.writeBytes(new byte[0]);
    }

    @Override
    public void handle(AbstractPacketHandler handler)
    {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

}
