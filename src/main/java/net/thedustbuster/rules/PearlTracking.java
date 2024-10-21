package net.thedustbuster.rules;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import net.thedustbuster.CarpetExtraExtrasSettings;
import net.thedustbuster.adaptors.minecraft.worldgen.ChunkHelper;
import net.thedustbuster.util.TextBuilder;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PearlTracking implements CarpetExtraExtrasRule {
  public static final PearlTracking INSTANCE = new PearlTracking();

  private static final Map<UUID, EnderPearlData> enderPearlData = new HashMap<>();

  @Override
  public void onTick() {
    if (enderPearlData.isEmpty() || !CarpetExtraExtrasSettings.trackEnderPearls) return;

    for (Iterator<Map.Entry<UUID, EnderPearlData>> it = enderPearlData.entrySet().iterator(); it.hasNext(); ) {
      Map.Entry<UUID, EnderPearlData> entry = it.next();
      EnderPearlData pearl = entry.getValue();

      if (!pearl.getEntity().isAlive()) it.remove();
    }
  }

  public static void updatePearl(Entity entity, Vec3 position, Vec3 velocity) {
    EnderPearlData pearl = enderPearlData.computeIfAbsent(entity.getUUID(), id -> new EnderPearlData(entity, position, velocity));
    pearl.updatePositionAndVelocity(position, velocity);
  }

  public Component[] createHUD() {
    Component[] hud = new Component[enderPearlData.size()];

    AtomicInteger index = new AtomicInteger(0);
    enderPearlData.forEach((u, p) -> {
      int i = index.getAndIncrement();

      String uuid = String.valueOf(u).substring(0, 12) + "...";
      String velocity = Mth.floor(p.getVelocity().length() * 20) + "m/s";
      String position = "(" + p.getIPosition().getX() + ", " + p.getIPosition().getY() + ", " + p.getIPosition().getZ() + ")";

      hud[i] = new TextBuilder()
        .addText("Pearl: ", List.of(ChatFormatting.DARK_AQUA))
        .addText(uuid, List.of(ChatFormatting.WHITE))
        .addText(" Velocity: ", List.of(ChatFormatting.DARK_AQUA))
        .addText(velocity, List.of(ChatFormatting.WHITE))
        .addText(" Position: ", List.of(ChatFormatting.DARK_AQUA))
        .addText(position, List.of(ChatFormatting.WHITE))
        .build();
    });

    return hud;
  }

  public static class EnderPearlData {
    private final Entity entity;
    private Vec3 position;
    private Vec3 velocity;

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

    public ChunkPos getChunkPos() {
      return ChunkHelper.calculateChunkPos(position);
    }

    @Override
    public String toString() {
      return "Entity: " + this.entity.getStringUUID() + "\nPosition: " + this.getPosition() + "\nVelocity: " + this.getVelocity() + "\nChunk Position: " + this.getChunkPos();
    }
  }
}
