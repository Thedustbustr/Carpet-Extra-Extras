package net.thedustbuster.rules.enderpearls;

import carpet.script.external.Carpet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.Vec3;
import net.thedustbuster.CarpetExtraExtrasSettings;

import java.util.*;

public final class PearlManager {
  private static final Map<UUID, EnderPearlData> enderPearlCache = new HashMap<>();
  private static final double HIGHSPEED_THRESHOLD = 100d; // blocks per tick

  public static Map<UUID, EnderPearlData> getEnderPearlCache() {
    return Collections.unmodifiableMap(enderPearlCache);
  }

  public static EnderPearlData updatePearl(ThrownEnderpearl entity, Vec3 position, Vec3 velocity) {
    EnderPearlData pearl = enderPearlCache.computeIfAbsent(entity.getUUID(), id -> new EnderPearlData(entity, position, velocity));
    pearl.updatePositionAndVelocity(position, velocity);
    if (pearl.isHighSpeed() && CarpetExtraExtrasSettings.enderPearlChunkLoadingFix) pearl.loadNextTravelChunk();
    return pearl;
  }

  public static void tick() {
    for (Iterator<Map.Entry<UUID, EnderPearlData>> it = enderPearlCache.entrySet().iterator(); it.hasNext(); ) {
      Map.Entry<UUID, EnderPearlData> entry = it.next(); EnderPearlData pearl = entry.getValue();

      if (!pearl.getEntity().isAlive()) {
        it.remove();
        pearl.dropAllChunks();
        continue;
      }

      if (pearl.isHighSpeed()) checkPearl(pearl);
    }
  }

  public static void checkPearl(EnderPearlData pearl) {
    pearl.dropUnusedChunks();

    if (!isEntityTickingChunk(pearl.getServerLevel(), pearl.getNextChunkPos())) {
      pearl.loadNextTravelChunk();
    }
  }

  public static boolean isHighSpeed(Vec3 velocity) {
    return Math.abs(velocity.x()) > HIGHSPEED_THRESHOLD || Math.abs(velocity.z()) > HIGHSPEED_THRESHOLD;
  }

  public static boolean isEntityTickingChunk(ServerLevel level, ChunkPos chunkPos) {
    return level.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, false) instanceof LevelChunk;
  }
}