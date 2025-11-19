package net.thedustbuster.adaptors.carpet;

import carpet.logging.HUDLogger;
import carpet.logging.LoggerRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.thedustbuster.adaptors.minecraft.text.TextBuffer;
import net.thedustbuster.libs.func.option.Option;

public final class LoggerHelper {
  public static Option<HUDLogger> createHUDLogger(String name) {
    return Option.of(FieldHelper.getField(LoggerRegistry.class, "__" + name))
      .fold(
        field -> Option.of(new HUDLogger(field.get(), name, null, null, false)),
        Option::empty
      );
  }

  public static Component[] createText(TextBuffer tb) {
    return new Component[]{ tb.build() };
  }

  public static Component[] createText(String text) {
    return new Component[]{ Component.literal(text) };
  }

  public static Component[] createText(String text, ChatFormatting formatting) {
    return new Component[]{ Component.literal(text).withStyle(formatting) };
  }
}
