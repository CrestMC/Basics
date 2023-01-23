package me.blurmit.basicsbungee.limbo.world;

public class World {

    private final Chunk[][] chunks;

    public World(int width, int length) {
        this.chunks = new Chunk[width][length];
    }

    public void setBlock(int blockX, int blockY, int blockZ, int blockID, byte blockData) {
        Chunk chunk = this.chunks[(blockX >> 4)][(blockZ >> 4)];

        if (chunk == null) {
            chunk = new Chunk(blockX >> 4, blockZ >> 4);
            chunks[(blockX >> 4)][(blockZ >> 4)] = chunk;
        }

        chunk.setBlock(blockX & 0xF, blockY, blockZ & 0xF, blockID, blockData);
    }

    public Chunk[][] getChunks() {
        return chunks;
    }

    public Chunk getChunkAtPosition(int chunkX, int chunkZ) {
        return chunks[(chunkX >> 4)][(chunkZ >> 4)];
    }

}
