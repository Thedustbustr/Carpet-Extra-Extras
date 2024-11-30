package net.thedustbuster.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.thedustbuster.CarpetExtraExtrasSettings;
import net.thedustbuster.adaptors.minecraft.worldgen.ChunkHelper;
import net.thedustbuster.rules.enderpearls.PearlManager;
import net.thedustbuster.util.Logger;
import org.apache.commons.logging.Log;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownEnderpearl.class)
public abstract class ThrownEnderpearlMixin extends ThrowableItemProjectile {
  @Shadow
  private long ticketTimer = 0L;

  @Unique
  private ThrownEnderpearl getSelf() {
    return (ThrownEnderpearl) (Object) this;
  }

  protected ThrownEnderpearlMixin(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
    super(entityType, level);
  }

  @Inject(method = "tick", at = @At(value = "HEAD"), cancellable = true)
  private void tick(CallbackInfo info) {
    if (!(this.level() instanceof ServerLevel serverLevel)) return;

    if (CarpetExtraExtrasSettings.enderPearlChunkLoadingFix || CarpetExtraExtrasSettings.trackEnderPearls) {
      updatePearlManager(info, serverLevel);
    }

    if (CarpetExtraExtrasSettings.pre21ThrowableEntityBehavior) {
      applyPre21Behavior(info);
    }
  }

  @Inject(method = "onRemoval", at = @At(value = "HEAD"))
  private void onRemoval(RemovalReason removalReason, CallbackInfo ci) {
    PearlManager.removePearl(this.getUUID());
  }

  @Unique
  private void updatePearlManager(CallbackInfo info, ServerLevel serverLevel) {
    Vec3 position = new Vec3(this.getX(), this.getY(), this.getZ());
    Vec3 velocity = this.getDeltaMovement();

    if (CarpetExtraExtrasSettings.trackEnderPearls) {
      PearlManager.updatePearl(this.getSelf(), position, velocity);
    }

    if (CarpetExtraExtrasSettings.enderPearlChunkLoadingFix) {
      PearlManager.tryLoadChunks(this.getSelf(), position, velocity);

      if (!PearlManager.isEntityTickingChunk(serverLevel, ChunkHelper.calculateChunkPos(position.add(velocity)))) {
        info.cancel(); // Cancel the rest of the original tick method
      }
    }
  }

  @Unique
  private void applyPre21Behavior(CallbackInfo info) {
    Entity entity = this.getOwner();
    if (entity instanceof ServerPlayer && !entity.isAlive() && ((ServerLevel) this.level()).getGameRules().getBoolean(GameRules.RULE_ENDER_PEARLS_VANISH_ON_DEATH)) {
      this.discard();
      info.cancel(); // Cancel the rest of the original tick method
      return;
    }

    int previousX = SectionPos.blockToSectionCoord(this.position().x());
    int previousZ = SectionPos.blockToSectionCoord(this.position().z());

    super.tick();

    if (this.isAlive() && shouldRenewTicket(this.position(), this.getDeltaMovement(), previousX, previousZ) && entity instanceof ServerPlayer serverPlayer) {
      this.ticketTimer = serverPlayer.registerAndUpdateEnderPearlTicket(this.getSelf());
    }

    info.cancel(); // Cancel the rest of the original tick method
  }

  @Unique
  private boolean shouldRenewTicket(Vec3 position, Vec3 velocity, int previousX, int previousZ) {
    BlockPos blockPos = BlockPos.containing(position);
    int currentX = SectionPos.blockToSectionCoord(blockPos.getX());
    int currentZ = SectionPos.blockToSectionCoord(blockPos.getZ());

    boolean timer = --this.ticketTimer <= 0L;
    boolean newChunk = previousX != currentX || previousZ != currentZ;
    boolean highSpeed = CarpetExtraExtrasSettings.enderPearlChunkLoadingFix
      && PearlManager.isHighSpeed(velocity);

    return !highSpeed && (timer || newChunk);
  }
}