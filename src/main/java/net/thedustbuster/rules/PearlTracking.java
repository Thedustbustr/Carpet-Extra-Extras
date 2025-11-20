package net.thedustbuster.rules;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.phys.Vec3;
import net.thedustbuster.adaptors.minecraft.text.TextBuffer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PearlTracking implements CEE_Rule {
  private static final Map<UUID, EnderPearlData> trackedEnderPearls = new HashMap<>();

  public static Map<UUID, EnderPearlData> getTrackedEnderPearls() {
    return Collections.unmodifiableMap(trackedEnderPearls);
  }

  public static void removedAllTrackedPearls() {
    trackedEnderPearls.clear();
  }

  public static void removePearl(UUID id) {
    trackedEnderPearls.remove(id);
  }

  public static void updatePearl(ThrownEnderpearl entity, Vec3 position, Vec3 velocity) {
    trackedEnderPearls.computeIfAbsent(entity.getUUID(), id -> new EnderPearlData(entity, position, velocity)).updatePositionAndVelocity(position, velocity);
  }

  public static Component[] createHUD() {
    return PearlTracking.getTrackedEnderPearls().entrySet().stream()
      .map(entry -> {
        UUID key = entry.getKey();
        EnderPearlData pearl = entry.getValue();

        String uuid = (key != null) ? key.toString().substring(0, Math.min(4, key.toString().length())) + "..." : "????";
        String velocity = Mth.floor(pearl.getVelocity().length() * 20) + "m/s";
        String position = String.format("(%d, %d, %d)", pearl.getIPosition().getX(), pearl.getIPosition().getY(), pearl.getIPosition().getZ());

        return new TextBuffer()
          .addText("Pearl: ", ChatFormatting.DARK_AQUA).addText(uuid, ChatFormatting.WHITE)
          .addText(" Velocity: ", ChatFormatting.DARK_AQUA).addText(velocity, ChatFormatting.WHITE)
          .addText(" Position: ", ChatFormatting.DARK_AQUA).addText(position, ChatFormatting.WHITE)
          .build();
      })
      .toArray(Component[]::new);
  }

  public static class EnderPearlData {
    private final ThrownEnderpearl entity;
    private Vec3 position;
    private Vec3 velocity;

    public EnderPearlData(ThrownEnderpearl entity, Vec3 position, Vec3 velocity) {
      this.entity = entity;
      this.position = position;
      this.velocity = velocity;
    }

    public Vec3 getPosition() { return position; }

    public Vec3 getVelocity() { return velocity; }

    public Vec3i getIPosition() { return new Vec3i(Mth.floor(this.position.x), Mth.floor(this.position.y), Mth.floor(this.position.z)); }

    public Vec3i getIVelocity() { return new Vec3i(Mth.floor(this.velocity.x), Mth.floor(this.velocity.y), Mth.floor(this.velocity.z)); }

    public void updatePositionAndVelocity(Vec3 position, Vec3 velocity) {
      this.position = position;
      this.velocity = velocity;
    }

    @Override
    public String toString() {
      return "Entity: " + this.entity.getStringUUID() + "\nPosition: " + this.getPosition() + "\nVelocity: " + this.getVelocity();
    }
  }
}