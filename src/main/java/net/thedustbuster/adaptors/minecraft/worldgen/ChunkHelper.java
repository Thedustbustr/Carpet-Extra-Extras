package net.thedustbuster.adaptors.minecraft.worldgen;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.thedustbuster.util.Logger;
import net.thedustbuster.util.TickDelayManager;
import net.thedustbuster.util.option.Option;

import java.util.HashMap;
import java.util.Map;

public final class ChunkHelper {
  private static final Map<ChunkPos, TickDelayManager.TickDelayTask> loadedChunks = new HashMap<>();

  private static Option<ServerLevel> isServerLevel(Level l) {
    if (l instanceof ServerLevel) return Option.of((ServerLevel) l);

    Logger.warn("Could not load/remove a chunk; the level " + l + " is not a server level! Things may not work properly!");
    return Option.empty();
  }

  public static void loadChunk(TicketType<ChunkPos> ticket, int ticks, ChunkPos chunkPos, int radius, Level level) {
    isServerLevel(level).whenDefined(serverLevel -> {
      serverLevel.getChunkSource().addRegionTicket(ticket, chunkPos, radius, chunkPos);
      loadedChunks.put(chunkPos, TickDelayManager.executeIn(ticks, () -> unloadChunk(ticket, chunkPos, radius, serverLevel)));
    });
  }

  public static void refreshChunkUnloadTimer(int ticks, ChunkPos chunkPos) {
    Option.of(loadedChunks.get(chunkPos)).whenDefined(task -> task.refresh(ticks));
  }

  public static void unloadChunk(TicketType<ChunkPos> ticket, ChunkPos chunkPos, int radius, Level level) {
    isServerLevel(level).whenDefined(serverLevel -> {
      serverLevel.getChunkSource().removeRegionTicket(ticket, chunkPos, radius, chunkPos);
      loadedChunks.remove(chunkPos);
    });
  }

  public static ChunkPos calculateChunkPos(Vec3 pos) {
    return new ChunkPos(Mth.floor(pos.x) >> 4, Mth.floor(pos.z) >> 4);
  }
}
