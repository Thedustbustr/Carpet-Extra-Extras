package net.thedustbuster.adaptors.minecraft.worldgen;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

public final class ChunkHelper {
  public static boolean isChunkAtLevel(ServerLevel lvl, ChunkLevel clvl, int cx, int cz) {
    return clvl == ChunkLevel.fromInt(lvl.getChunkSource().chunkMap.getDistanceManager().getChunkLevel(ChunkPos.asLong(cx, cz), false));
  }
}