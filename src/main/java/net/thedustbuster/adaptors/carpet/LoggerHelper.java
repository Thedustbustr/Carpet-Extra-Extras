package net.thedustbuster.adaptors.carpet;

import carpet.logging.HUDLogger;
import carpet.logging.LoggerRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.thedustbuster.util.TextBuilder;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public final class LoggerHelper {
  @Nullable
  public static HUDLogger createHUDLogger(String name) {
    Field field = FieldHelper.getField(LoggerRegistry.class, "__" + name);

    if (field == null) { return null; }

    return new HUDLogger(field, name, null, null, false);
  }

  public static Component[] createText(TextBuilder tb) {
    return new Component[]{tb.build()};
  }

  public static Component[] createText(String text) {
    return new Component[]{Component.literal(text)};
  }

  public static Component[] createText(String text, ChatFormatting formatting) {
    return new Component[]{Component.literal(text).withStyle(formatting)};
  }
}
