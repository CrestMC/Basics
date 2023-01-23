package me.blurmit.basicsbungee.limbo.world;

import net.md_5.bungee.api.ProxyServer;
import se.llbit.nbt.CompoundTag;

public class Schematic {

    public static World parseWorld(CompoundTag tag) {
        ProxyServer.getInstance().getLogger().info(tag.toString());

        short width = tag.get("Schematic").get("Width").shortValue();
        short length = tag.get("Schematic").get("Length").shortValue();
        short height = tag.get("Schematic").get("Height").shortValue();

        byte[] blocks = tag.get("Schematic").get("Blocks").byteArray();
        byte[] blocksData = tag.get("Schematic").get("Data").byteArray();

        ProxyServer.getInstance().getLogger().info("Creating world with bounds w=" + width + ", l=" + length);
        World world = new World(width, length);

        int blockY;
        int blockZ;

        for (int blockX = 0; blockX < width; blockX++) {
            for (blockY = 0; blockY < height; blockY++) {
                for (blockZ = 0; blockZ < length; blockZ++) {
                    int blockIndex = blockY * width * length + blockZ * width + blockX;
                    world.setBlock(blockX, blockY, blockZ, blocks[blockIndex] < 0 ? blocks[blockIndex] + 256 : blocks[blockIndex], blocksData[blockIndex]);
                }
            }
        }

        return world;
    }

}
