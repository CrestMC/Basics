package me.blurmit.basicsbungee.limbo.world;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.md_5.bungee.protocol.DefinedPacket;

import java.util.Arrays;

public class Chunk {

    private final int chunkX;
    private final int chunkZ;

    private byte[] chunkMap;
    private final long[][] chunkMapPalette;

    public Chunk(int chunkX, int chunkZ) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;

        this.chunkMap = new byte[196864];
        this.chunkMapPalette = new long[16][832];

        Arrays.fill(chunkMap, (byte) 0);

        for (long[] tab : chunkMapPalette) {
            Arrays.fill(tab, 0L);
        }
    }

    public void setBlock(int blockX, int blockY, int blockZ, int blockID, int blockMeta) {
        int index = blockX + blockY * 16 * 16 + blockZ * 16;
        index *= 2;

        chunkMap[index] = ((byte) ((byte) (blockID << 4) | blockMeta));
        chunkMap[(index + 1)] = ((byte) (blockID >> 4));

        int section = blockY / 16;
        index = blockX + blockZ * 16 + blockY % 16 * 16 * 16;
        index *= 13;
        int l = index / 64;
        index = index % 64;

        for (int i = 0; i < 13; ++i) {
            if (index == 64) {
                index = 0;
                l++;
            }

            long value;
            if (i < 4) {
                value = (blockMeta & (1L << i)) != 0 ? 1 : 0;
            } else {
                value = (blockID & (1L << (i - 4))) != 0 ? 1 : 0;
            }

            chunkMapPalette[section][l] = chunkMapPalette[section][l] & ~(1L << index);
            chunkMapPalette[section][l] = chunkMapPalette[section][l] | (value << index);

            index++;
        }
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }

    public byte[] getChunkMapPalette() {
        ByteBuf buf = Unpooled.buffer();

        for (int i = 0; i < 16; ++i) {
            buf.writeByte(13);
            DefinedPacket.writeVarInt(0, buf);
            DefinedPacket.writeVarInt(chunkMapPalette[i].length, buf);

            for (long value : chunkMapPalette[i]) {
                buf.writeLong(value);
            }

            for (int j = 0; j < 2048; ++j) {
                buf.writeByte(0);
            }
        }

        return buf.array();
    }

    public byte[] getChunkMap() {
        return chunkMap;
    }

    public void setChunkMap(byte[] chunkMap) {
        this.chunkMap = chunkMap;
    }

}
