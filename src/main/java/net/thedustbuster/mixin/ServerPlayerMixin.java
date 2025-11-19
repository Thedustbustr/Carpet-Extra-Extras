package net.thedustbuster.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.thedustbuster.commands.CamCommand;
import net.thedustbuster.libs.func.option.Option;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
  @Unique
  private ServerPlayer self() {
    return (ServerPlayer) (Object) this;
  }

  @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
  private void addAdditionalSaveData(ValueOutput valueOutput, CallbackInfo ci) {
    CamCommand.getPlayerData(self().getUUID()).whenDefined(data -> {
      valueOutput.store("FreecamData", CamCommand.FreecamData.CODEC, data);
    });
  }

  @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
  private void readAdditionalSaveData(ValueInput valueInput, CallbackInfo ci) {
    Option.of(valueInput.read("FreecamData", CamCommand.FreecamData.CODEC)).whenDefined(data -> {
      CamCommand.addPlayerData(self().getUUID(), data);
    });
  }
}