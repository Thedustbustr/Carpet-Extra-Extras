package net.thedustbuster.adaptors.carpet;

import carpet.CarpetServer;
import carpet.logging.LoggerRegistry;
import net.thedustbuster.BetterCarpetBots;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public final class FieldHelper {
  @Nullable
  public static Field getField(Class<?> c, String name) {
    return readField(c, name);
  }

  @Nullable
  public static Field getField(Class<?> c, String name, boolean forceAccessible) {
    Field f = readField(c, name);
    if (f == null) { return null; }
    else if (!forceAccessible) { return f; }
    else {
      f.setAccessible(true);
      return f;
    }
  }

  @Nullable
  private static Field readField(Class<?> c, String name) {
    try {
      if (c == null) {
        BetterCarpetBots.LOGGER.error("Object cannot be null");
        return null;
      }

      return c.getDeclaredField(name);

    } catch (NoSuchFieldException e) {
      BetterCarpetBots.LOGGER.error("Field '{}' does not exist in object: {}", name, c);
    }

    return null;
  }
}