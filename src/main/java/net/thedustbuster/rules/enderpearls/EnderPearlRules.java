package net.thedustbuster.rules.enderpearls;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.thedustbuster.CarpetExtraExtrasSettings;
import net.thedustbuster.rules.CEE_Rule;
import net.thedustbuster.util.TextBuilder;

import java.util.concurrent.atomic.AtomicInteger;

public class EnderPearlRules implements CEE_Rule {
  public static final EnderPearlRules INSTANCE = new EnderPearlRules();

  @Override
  public void onTick() {
    if (CarpetExtraExtrasSettings.enderPearlChunkLoadingFix) {
      PearlManager.tick();
    }
  }

  public Component[] createHUD() {
    Component[] hud = new Component[PearlManager.getTrackedEnderPearls().size()];

    AtomicInteger index = new AtomicInteger(0);
    PearlManager.getTrackedEnderPearls().forEach((k, v) -> {
      int i = index.getAndIncrement();

      String travel = PearlManager.getHighSpeedPearls().containsKey(k) ? "[High Speed] " : "";
      String uuid = String.valueOf(k).substring(0, 4) + "...";
      String velocity = Mth.floor(v.getVelocity().length() * 20) + "m/s";
      String position = "(" + v.getIPosition().getX() + ", " + v.getIPosition().getY() + ", " + v.getIPosition().getZ() + ")";

      hud[i] = new TextBuilder()
        .addText(travel, ChatFormatting.DARK_PURPLE)
        .addText("Pearl: ", ChatFormatting.DARK_AQUA)
        .addText(uuid, ChatFormatting.WHITE)
        .addText(" Velocity: ", ChatFormatting.DARK_AQUA)
        .addText(velocity, ChatFormatting.WHITE)
        .addText(" Position: ", ChatFormatting.DARK_AQUA)
        .addText(position, ChatFormatting.WHITE)
        .build();
    });

    return hud;
  }
}