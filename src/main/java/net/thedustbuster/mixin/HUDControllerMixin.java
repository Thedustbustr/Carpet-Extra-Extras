package net.thedustbuster.mixin;

import carpet.CarpetServer;
import carpet.logging.HUDController;
import carpet.logging.LoggerRegistry;
import net.minecraft.ChatFormatting;
import net.thedustbuster.BetterCarpetBots;
import net.thedustbuster.adaptors.carpet.FieldHelper;
import net.thedustbuster.adaptors.carpet.LoggerHelper;
import net.thedustbuster.util.TextBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

@Mixin(HUDController.class)
public abstract class HUDControllerMixin {
  @Inject(method = "update_hud",
          at = @At(
                  value = "INVOKE",
                  target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V",
                  shift = At.Shift.AFTER
          ),
          slice = @Slice(
                  from = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V", shift = At.Shift.BEFORE),
                  to = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V", shift = At.Shift.AFTER, by = 2)
          ),
          remap = false
  )
  private static void update_hud(CallbackInfo ci) throws IllegalAccessException {
    Field field = FieldHelper.getField(LoggerRegistry.class, "__bots", true);

    if (field != null && field.getBoolean(null)) {
      TextBuilder text = new TextBuilder()
              .addText("Players: ", List.of(ChatFormatting.WHITE))
              .addText(String.valueOf(CarpetServer.minecraft_server.getPlayerCount() - BetterCarpetBots.getBots()), List.of(ChatFormatting.DARK_GREEN))
              .addText(" Bots: ", List.of(ChatFormatting.WHITE))
              .addText(String.valueOf(BetterCarpetBots.getBots()), List.of(ChatFormatting.GOLD));

      LoggerRegistry.getLogger("bots").log(() -> LoggerHelper.createText(text));
    }
  }
}
