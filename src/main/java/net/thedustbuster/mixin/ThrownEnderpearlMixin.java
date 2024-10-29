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
import net.thedustbuster.rules.betterpearls.PearlManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownEnderpearl.class)
public abstract class ThrownEnderpearlMixin extends ThrowableItemProjectile {
  @Shadow
  private long ticketTimer = 0L;

  protected ThrownEnderpearlMixin(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
    super(entityType, level);
  }

  @Inject(method = "tick()V", at = @At(value = "HEAD"), cancellable = true)
  private void tick(CallbackInfo info) {
    if (CarpetExtraExtrasSettings.enderPearlChunkLoadingFix && this.level() instanceof ServerLevel serverLevel) {
      applyEnderPearlChunkLoadingFix(info, serverLevel);
    } else if (CarpetExtraExtrasSettings.trackEnderPearls && this.level() instanceof ServerLevel serverLevel) {
      applyEnderPearlChunkLoadingFix(info, serverLevel);
    }

    if (CarpetExtraExtrasSettings.Pre21ThrowableEntityBehaviorReintroduced) {
      applyPre21ThrowableEntityBehaviorReintroduced();
      info.cancel();
    }
  }

  private void applyEnderPearlChunkLoadingFix(CallbackInfo info, ServerLevel serverLevel) {
    Vec3 position = new Vec3(this.getX(), this.getY(), this.getZ());
    Vec3 velocity = this.getDeltaMovement();

    PearlManager.EnderPearlData pearlData = PearlManager.updatePearl(this, position, velocity);

    if (!PearlManager.isEntityTickingChunk(serverLevel, pearlData.getNextChunkPos()) && CarpetExtraExtrasSettings.enderPearlChunkLoadingFix) {
      info.cancel();
    }
  }

  private void applyPre21ThrowableEntityBehaviorReintroduced() {
    Entity entity = this.getOwner();
    if (entity instanceof ServerPlayer && !entity.isAlive() && ((ServerLevel) this.level()).getGameRules().getBoolean(GameRules.RULE_ENDER_PEARLS_VANISH_ON_DEATH)) {
      this.discard();
    } else {
      int i = SectionPos.blockToSectionCoord(this.position().x());
      int j = SectionPos.blockToSectionCoord(this.position().z());

      super.tick();

      if (this.isAlive()) {
        BlockPos blockPos = BlockPos.containing(this.position());
        if ((--this.ticketTimer <= 0L || (!CarpetExtraExtrasSettings.enderPearlChunkLoadingFix && (i != SectionPos.blockToSectionCoord(blockPos.getX()) || j != SectionPos.blockToSectionCoord(blockPos.getZ())))) && entity instanceof ServerPlayer) {
          ServerPlayer serverPlayer2 = (ServerPlayer)entity;
          System.out.println("loaded");

          this.ticketTimer = serverPlayer2.registerAndUpdateEnderPearlTicket((ThrownEnderpearl) (Object) this);
        }

      }
    }
  }

//  private boolean shouldProcessTick() {
//    Level level = this.level();
//    Vec3 velocity = this.getDeltaMovement();
//
//    return CarpetExtraExtrasSettings.betterEnderPearlChunkLoading && level instanceof ServerLevel && hasHorizontalMotion(velocity);
//  }
//
//  private boolean hasHorizontalMotion(Vec3 velocity) {
//    return Math.abs(velocity.x) > 0.01 || Math.abs(velocity.z) > 0.01;
//  }
}