package net.thedustbuster.rules.bpcl;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.thedustbuster.CarpetExtraExtrasSettings;
import net.thedustbuster.rules.CarpetExtraExtrasRule;
import net.thedustbuster.util.TextBuilder;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BetterPearlChunkLoading implements CarpetExtraExtrasRule {
  public static final BetterPearlChunkLoading INSTANCE = new BetterPearlChunkLoading();

  @Override
  public void onTick() {
    if (CarpetExtraExtrasSettings.betterEnderPearlChunkLoading) {
      PearlManager.tick();
    }
  }

  public Component[] createHUD() {
    Component[] hud = new Component[PearlManager.getEnderPearlCache().size()];

    AtomicInteger index = new AtomicInteger(0);
    PearlManager.getEnderPearlCache().forEach((u, p) -> {
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
}
