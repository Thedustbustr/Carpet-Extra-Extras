package net.thedustbuster.rules.enderpearls;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.Vec3;
import net.thedustbuster.CarpetExtraExtrasSettings;
import net.thedustbuster.util.option.Option;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PearlManager {
  private static final Map<UUID, EnderPearlData> trackedEnderPearls = new HashMap<>();
  private static final Map<UUID, EnderPearlData> highSpeedPearls = new HashMap<>();

  private static final double HIGHSPEED_THRESHOLD = 100d; // blocks per tick

  public static Map<UUID, EnderPearlData> getTrackedEnderPearls() {
    return Collections.unmodifiableMap(trackedEnderPearls);
  }

  public static Map<UUID, EnderPearlData> getHighSpeedPearls() {
    return Collections.unmodifiableMap(highSpeedPearls);
  }

  public static void removedAllTrackedPearls() {
    trackedEnderPearls.clear();
  }

  public static void removedAllHighSpeedPearls() {
    highSpeedPearls.values().forEach(EnderPearlData::dropAllChunks);
    trackedEnderPearls.clear();
    highSpeedPearls.clear();
  }

  public static void removePearl(UUID id) {
    removePearl(id, true);
  }

  private static void removePearl(UUID id, boolean removedTracked) {
    highSpeedPearls.remove(id);
    if (removedTracked) trackedEnderPearls.remove(id);
  }

  private static EnderPearlData flagAsHighSpeed(ThrownEnderpearl entity, Vec3 position, Vec3 velocity) {
    return highSpeedPearls.computeIfAbsent(entity.getUUID(), id -> new EnderPearlData(entity, position, velocity))
      .updatePositionAndVelocity(position, velocity);
  }

  public static void tick() {
    if (!highSpeedPearls.isEmpty()) {
      highSpeedPearls.forEach((k, v) -> checkPearl(v));
    }
  }

  public static void updatePearl(ThrownEnderpearl entity, Vec3 position, Vec3 velocity) {
    Option.of(getHighSpeedPearls().get(entity.getUUID()))
      .fold(
        pearl -> trackedEnderPearls.computeIfAbsent(entity.getUUID(), id -> pearl).updatePositionAndVelocity(position, velocity),
        () -> trackedEnderPearls.computeIfAbsent(entity.getUUID(), id -> new EnderPearlData(entity, position, velocity)).updatePositionAndVelocity(position, velocity)
      );
  }

  public static void tryLoadChunks(ThrownEnderpearl entity, Vec3 position, Vec3 velocity) {
    if (isHighSpeed(velocity)) {
      checkPearl(flagAsHighSpeed(entity, position, velocity));
    } else {
      removePearl(entity.getUUID(), !CarpetExtraExtrasSettings.trackEnderPearls);
    }
  }

  private static void checkPearl(EnderPearlData pearl) {
    pearl.dropUnusedChunks();

    if (!isEntityTickingChunk(pearl.getServerLevel(), pearl.getChunkPos())) {
      pearl.loadCurrentTravelChunk();
    }

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