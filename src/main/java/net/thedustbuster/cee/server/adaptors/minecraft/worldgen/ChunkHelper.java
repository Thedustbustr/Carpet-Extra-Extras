package net.thedustbuster.cee.server.adaptors.minecraft.worldgen;

import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

public final class ChunkHelper {
  private static DistanceManager distanceManager(ServerLevel lvl) {
    return lvl.getChunkSource().chunkMap.getDistanceManager();
  }

  public static boolean isEntityTicking(ServerLevel lvl, int cx, int cz) {
    return distanceManager(lvl).inEntityTickingRange(ChunkPos.asLong(cx, cz));
  }

  public static boolean isLazy(ServerLevel lvl, int cx, int cz) {
    return distanceManager(lvl).inBlockTickingRange(ChunkPos.asLong(cx, cz)) && !isEntityTicking(lvl, cx, cz);
  }
}