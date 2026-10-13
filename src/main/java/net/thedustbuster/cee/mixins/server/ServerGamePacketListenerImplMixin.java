package net.thedustbuster.cee.mixins.server;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.phys.Vec3;
import net.thedustbuster.cee.server.CarpetExtraExtrasSettings;
import net.thedustbuster.cee.server.rules.PistonPushDesyncFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {
  @Shadow
  public ServerPlayer player;

  // ordinal 2 is the teleport back to the server's position after a rejected move
  @WrapOperation(
    method = "handlePlayerPositionChange",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;teleport(DDDFF)V",
      ordinal = 2
    )
  )
  private void cee$handlePositionBehindPistonPush(
    ServerGamePacketListenerImpl instance, double x, double y, double z, float yRot, float xRot, Operation<Void> original,
    @Local(argsOnly = true, name = "requestedX") double requestedX, @Local(argsOnly = true, name = "requestedY") double requestedY, @Local(argsOnly = true, name = "requestedZ") double requestedZ
  ) {
    boolean handled = CarpetExtraExtrasSettings.pistonPushDesyncFix
      && PistonPushDesyncFix.handleRejectedPos(this.player, new Vec3(x, y, z), new Vec3(requestedX, requestedY, requestedZ), yRot, xRot);

    if (!handled) original.call(instance, x, y, z, yRot, xRot);
  }
}
