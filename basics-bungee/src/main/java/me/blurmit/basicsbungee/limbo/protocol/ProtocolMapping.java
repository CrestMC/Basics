package me.blurmit.basicsbungee.limbo.protocol;

import lombok.Data;

@Data
public class ProtocolMapping {

    private final int protocolVersion;
    private final int packetID;

}
