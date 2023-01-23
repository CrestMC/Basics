package me.blurmit.basicsbungee.limbo.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PacketPlayerPosition extends DefinedPacket {

    private double x;
    private double y;
    private double z;

    private float yaw;
    private float pitch;

    private byte data;
    private int teleportedID;

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        x = buf.readDouble();
        y = buf.readDouble();
        z = buf.readDouble();

        yaw = buf.readFloat();
        pitch = buf.readFloat();

        data = buf.readByte();

        if (protocolVersion > ProtocolConstants.MINECRAFT_1_9) {
            teleportedID = DefinedPacket.readVarInt(buf);
        }
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeFloat(yaw);
        buf.writeFloat(pitch);
        buf.writeByte(data);

        if (protocolVersion > ProtocolConstants.MINECRAFT_1_9) {
            DefinedPacket.writeVarInt(teleportedID, buf);
        }
    }

    @Override
    public void handle(AbstractPacketHandler handler) {
        // Yeah, ok.
    }

}
