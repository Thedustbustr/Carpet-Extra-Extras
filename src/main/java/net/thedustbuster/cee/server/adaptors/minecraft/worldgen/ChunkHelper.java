package net.thedustbuster.cee.server.adaptors.minecraft.worldgen;

import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

public final class ChunkHelper {
  private static DistanceManager distanceManager(ServerLevel lvl) {
    return lvl.getChunkSource().chunkMap.getDistanceManager();
  }

  private static long getChunkKey(int x, int z) {
    return ChunkPos.pack(x, z);
  }

  public static boolean isEntityTicking(ServerLevel lvl, int cx, int cz) {
    return distanceManager(lvl).inEntityTickingRange(getChunkKey(cx, cz));
  }

  public static boolean isLazyTicking(ServerLevel lvl, int cx, int cz) {
    return distanceManager(lvl).inBlockTickingRange(getChunkKey(cx, cz)) && !isEntityTicking(lvl, cx, cz);
  }
}