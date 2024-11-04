package net.thedustbuster.rules.enderpearls;

import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import net.thedustbuster.adaptors.minecraft.worldgen.ChunkHelper;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class EnderPearlData {
  private static final TicketType<ChunkPos> ENDER_PEARL_TRAVEL_TICKET = TicketType.create("ender_pearl_travel", Comparator.comparingLong(ChunkPos::toLong));
  private static final int TICKS_TO_CHUNK_UNLOAD = 6;

  private final ThrownEnderpearl entity;
  private Vec3 position;
  private Vec3 velocity;
  private final Set<ChunkPos> loadedChunks = new HashSet<>();

  public EnderPearlData(ThrownEnderpearl entity, Vec3 position, Vec3 velocity) {
    this.entity = entity;
    this.position = position;
    this.velocity = velocity;
  }

  public Vec3 getPosition() { return position; }

  public Vec3 getVelocity() { return velocity; }

  public Vec3i getIPosition() { return new Vec3i(Mth.floor(this.position.x), Mth.floor(this.position.y), Mth.floor(this.position.z)); }

  public Vec3i getIVelocity() { return new Vec3i(Mth.floor(this.velocity.x), Mth.floor(this.velocity.y), Mth.floor(this.velocity.z)); }

  public ServerLevel getServerLevel() {
    return (ServerLevel) entity.level();
  }

  public EnderPearlData updatePositionAndVelocity(Vec3 position, Vec3 velocity) {
    this.position = position;
    this.velocity = velocity;

    return this;
  }

  public void loadNextTravelChunk() {
    addLoadedTravelChunk(getNextChunkPos());
  }

  public void loadCurrentTravelChunk() {
    addLoadedTravelChunk(getChunkPos());
  }

  public void addLoadedTravelChunk(ChunkPos chunkPos) {
    if (loadedChunks.contains(chunkPos)) {
      ChunkHelper.refreshChunkUnloadTimer(TICKS_TO_CHUNK_UNLOAD, chunkPos);
    } else {
      ChunkHelper.loadChunk(ENDER_PEARL_TRAVEL_TICKET, TICKS_TO_CHUNK_UNLOAD, chunkPos, 2, entity.level());
      loadedChunks.add(getNextChunkPos());
    }
  }

  public ChunkPos getChunkPos() {
    return ChunkHelper.calculateChunkPos(position);
  }

  public ChunkPos getNextChunkPos() {
    return ChunkHelper.calculateChunkPos(position.add(velocity));
  }

  public void dropUnusedChunks() {
    Set<ChunkPos> toRemove = this.loadedChunks.stream()
      .filter(c -> c != null && !c.equals(this.getChunkPos()) && !c.equals(this.getNextChunkPos()))
      .collect(Collectors.toSet());

    toRemove.forEach(this::removeLoadedTravelChunk);
  }

  public void dropAllChunks() {
    new HashSet<>(loadedChunks).forEach(this::removeLoadedTravelChunk);
  }

  private void removeLoadedTravelChunk(ChunkPos chunkPos) {
    loadedChunks.remove(chunkPos);
    ChunkHelper.unloadChunk(ENDER_PEARL_TRAVEL_TICKET, chunkPos, 2, entity.level());
  }

  @Override
  public String toString() {
    return "Entity: " + this.entity.getStringUUID() + "\nPosition: " + this.getPosition() + "\nVelocity: " + this.getVelocity() + "\nChunk Position: " + this.getChunkPos();
  }
}