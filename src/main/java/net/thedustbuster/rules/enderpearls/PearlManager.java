package net.thedustbuster.rules.enderpearls;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.Vec3;
import net.thedustbuster.CarpetExtraExtrasSettings;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PearlManager {
  private static final Map<UUID, EnderPearlData> enderPearlCache = new HashMap<>();

  public static Map<UUID, EnderPearlData> getEnderPearlCache() {
    return Collections.unmodifiableMap(enderPearlCache);
  }

  public static EnderPearlData getEnderPearl(UUID id) {
    return enderPearlCache.get(id);
  }

  public static EnderPearlData updatePearl(ThrownEnderpearl entity, Vec3 position, Vec3 velocity) {
    EnderPearlData pearl = enderPearlCache.computeIfAbsent(entity.getUUID(), id -> new EnderPearlData(entity, position, velocity));
    pearl.updatePositionAndVelocity(position, velocity);

    if (pearl.isHighSpeed() && CarpetExtraExtrasSettings.enderPearlChunkLoadingFix) {
      pearl.loadNextTravelChunk();
    }

    return pearl;
  }

  public static void tick() {
    enderPearlCache.entrySet().removeIf(entry -> {
      EnderPearlData pearl = entry.getValue();

      if (!pearl.getEntity().isAlive()) {
        pearl.dropAllChunks();
        return true;
      }

      if (pearl.isHighSpeed()) {
        checkPearl(pearl);
      }

      return false;
    });
  }

  public static void checkPearl(EnderPearlData pearl) {
    pearl.dropUnusedChunks();

    if (!isEntityTickingChunk(pearl.getServerLevel(), pearl.getNextChunkPos())) {
      pearl.loadNextTravelChunk();
    }
  }

  public static boolean isEntityTickingChunk(ServerLevel level, ChunkPos chunkPos) {
    return level.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, false) instanceof LevelChunk;
  }
}