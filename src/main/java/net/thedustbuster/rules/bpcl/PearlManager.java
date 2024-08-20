package net.thedustbuster.rules.bpcl;

import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.Vec3;
import net.thedustbuster.adaptors.minecraft.worldgen.ChunkHelper;

import java.util.*;

public final class PearlManager {
  public static final TicketType<ChunkPos> ENDER_PEARL_TICKET = TicketType.create("ender_pearl", Comparator.comparingLong(ChunkPos::toLong));
  private static final Map<UUID, EnderPearlData> enderPearlCache = new HashMap<>();

  public static Map<UUID, EnderPearlData> getEnderPearlCache() {
    return Collections.unmodifiableMap(enderPearlCache);
  }

  // Runs every tick
  public static void tick() {
    for (Iterator<Map.Entry<UUID, EnderPearlData>> it = enderPearlCache.entrySet().iterator(); it.hasNext(); ) {
      Map.Entry<UUID, EnderPearlData> entry = it.next(); EnderPearlData pearl = entry.getValue();

      // If the pearl lands, remove it from the cache
      if (!pearl.getEntity().isAlive()) { pearl.dropAllChunks(); it.remove(); continue; }

      if (pearl.getEntity().level() instanceof ServerLevel serverLevel)
        checkPearl(serverLevel, pearl);
    }
  }

  public static void checkPearl(ServerLevel level, EnderPearlData pearl) {
    List<ChunkPos> toRemove = pearl.loadedChunks.stream()
            .filter(c -> c != null && !c.equals(pearl.getChunkPos()) && !c.equals(pearl.getNextChunkPos()))
            .toList();

    toRemove.forEach(pearl::removeLoadedChunk);

    if (!isEntityTickingChunk(level, pearl.getNextChunkPos())) {
      pearl.addLoadedChunk(pearl.getNextChunkPos());
    }
  }

  public static boolean isEntityTickingChunk(ServerLevel level, ChunkPos chunkPos) {
    return level.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, false) instanceof LevelChunk;
  }


  public static EnderPearlData updatePearl(Entity entity, Vec3 position, Vec3 velocity) {
    EnderPearlData pearl = enderPearlCache.computeIfAbsent(entity.getUUID(), id -> new EnderPearlData(entity, position, velocity));
    pearl.updatePositionAndVelocity(position, velocity);
    pearl.loadNextChunk();
    return pearl;
  }

  public static class EnderPearlData {
    private final Entity entity;
    private Vec3 position;
    private Vec3 velocity;
    private final Set<ChunkPos> loadedChunks = new HashSet<>();

    protected EnderPearlData(Entity entity, Vec3 position, Vec3 velocity) {
      this.entity = entity;
      this.position = position;
      this.velocity = velocity;
    }

    public Entity getEntity() { return this.entity; }

    public Vec3 getPosition() { return position; }

    public Vec3 getVelocity() { return velocity; }

    public Vec3i getIPosition() { return new Vec3i(Mth.floor(this.position.x), Mth.floor(this.position.y), Mth.floor(this.position.z)); }

    public Vec3i getIVelocity() { return new Vec3i(Mth.floor(this.velocity.x), Mth.floor(this.velocity.y), Mth.floor(this.velocity.z)); }

    protected void updatePositionAndVelocity(Vec3 position, Vec3 velocity) {
      this.position = position;
      this.velocity = velocity;
    }

    protected void loadNextChunk() {
      addLoadedChunk(getNextChunkPos());
    }

    protected void addLoadedChunk(ChunkPos chunkPos) {
      if (loadedChunks.add(chunkPos)) {
        ChunkHelper.loadChunk(ENDER_PEARL_TICKET, chunkPos, 2, entity.level());
      }
    }

    protected void removeLoadedChunk(ChunkPos chunkPos) {
      if (loadedChunks.remove(chunkPos)) {
        ChunkHelper.unloadChunk(ENDER_PEARL_TICKET, chunkPos, 2, entity.level());
      }
    }

    protected ChunkPos getChunkPos() {
      return ChunkHelper.calculateChunkPos(position);
    }

    public ChunkPos getNextChunkPos() {
      return ChunkHelper.calculateChunkPos(position.add(velocity));
    }

    protected void dropAllChunks() {
      new HashSet<>(loadedChunks).forEach(this::removeLoadedChunk);
    }

    @Override
    public String toString() {
      return "Entity: " + this.entity.getStringUUID() + "\nPosition: " + this.getPosition() + "\nVelocity: " + this.getVelocity() + "\nChunk Position: " + this.getChunkPos();
    }
  }
}
