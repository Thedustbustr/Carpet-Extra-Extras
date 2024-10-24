package net.thedustbuster.adaptors.minecraft.worldgen;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.thedustbuster.util.Logger;

public final class ChunkHelper {
//  private static final Set<ChunkPos> loadedChunks = new HashSet<>();

  private static boolean isServerLevel(Level l) {
    if (l instanceof ServerLevel) return true;

    Logger.warn("Could not load/remove a chunk; the level " + l + " is not a server level! Things may not work properly!");
    return false;
  }

  public static void loadChunk(TicketType<ChunkPos> ticket, ChunkPos c, int radius, Level level) {
    if (!isServerLevel(level)) return;

    ((ServerLevel) level).getChunkSource().addRegionTicket(ticket, c, radius, c);
//    loadedChunks.add(c);
  }

  public static void unloadChunk(TicketType<ChunkPos> ticket, ChunkPos c, int radius, Level level) {
    if (!isServerLevel(level)) return;

    ((ServerLevel) level).getChunkSource().removeRegionTicket(ticket, c, radius, c);
//    loadedChunks.remove(c);
  }

  public static ChunkPos calculateChunkPos(Vec3 pos) {
    return new ChunkPos(Mth.floor(pos.x) >> 4, Mth.floor(pos.z) >> 4);
  }
}
