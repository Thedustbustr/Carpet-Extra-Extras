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
import net.thedustbuster.rules.enderpearls.EnderPearlData;
import net.thedustbuster.rules.enderpearls.PearlManager;
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

  @Inject(method = "tick()V", at = @At(value = "HEAD"), cancellable = true)
  private void tick(CallbackInfo info) {
    if (!(this.level() instanceof ServerLevel serverLevel)) return;

    if (CarpetExtraExtrasSettings.enderPearlChunkLoadingFix || CarpetExtraExtrasSettings.trackEnderPearls) {
      updatePearlManager(info, serverLevel);
    }

    if (CarpetExtraExtrasSettings.pre21ThrowableEntityBehavior) {
      applyPre21Behavior(info);
    }
  }

  @Unique
  private void updatePearlManager(CallbackInfo info, ServerLevel serverLevel) {
    Vec3 position = new Vec3(this.getX(), this.getY(), this.getZ());
    Vec3 velocity = this.getDeltaMovement();

    EnderPearlData pearlData = PearlManager.updatePearl(this.getSelf(), position, velocity);

    if (CarpetExtraExtrasSettings.enderPearlChunkLoadingFix && !PearlManager.isEntityTickingChunk(serverLevel, pearlData.getNextChunkPos())) {
      info.cancel(); // Cancel the rest of the original tick method
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

    if (this.isAlive() && shouldRenewTicket(this.position(), previousX, previousZ) && entity instanceof ServerPlayer serverPlayer) {
      this.ticketTimer = serverPlayer.registerAndUpdateEnderPearlTicket(this.getSelf());
    }

    info.cancel(); // Cancel the rest of the original tick method
  }

  @Unique
  private boolean shouldRenewTicket(Vec3 position, int previousX, int previousZ) {
    BlockPos blockPos = BlockPos.containing(position);
    int currentX = SectionPos.blockToSectionCoord(blockPos.getX());
    int currentZ = SectionPos.blockToSectionCoord(blockPos.getZ());

    boolean timer = --this.ticketTimer <= 0L;
    boolean newChunk = previousX != currentX || previousZ != currentZ;
    boolean highSpeed = CarpetExtraExtrasSettings.enderPearlChunkLoadingFix
            && PearlManager.getEnderPearl(this.getUUID()).isHighSpeed();

    return !highSpeed && (timer || newChunk);
  }
}