package net.thedustbuster.adaptors.carpet;

import net.thedustbuster.CarpetExtraExtrasServer;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public final class FieldHelper {
  @Nullable
  public static Field getField(Class<?> c, String name) { return readField(c, name); }

  @Nullable
  public static Field getField(Class<?> c, String n, boolean fa) { return getField(c, n, fa, false); }

  @Nullable
  public static Field getField(Class<?> c, String name, boolean forceAccessible, boolean log) {
    Field f = readField(c, name, log);
    if (f == null) return null;
    else if (!forceAccessible) return f;
    else {
      f.setAccessible(true);
      return f;
    }
  }

  @Nullable
  private static Field readField(Class<?> c, String n) {
    return readField(c, n, false);
  }

  @Nullable
  private static Field readField(Class<?> c, String name, boolean log) {
    try {
      if (c == null) {
        CarpetExtraExtrasServer.LOGGER.error("Object cannot be null");
        return null;
      }

      return c.getDeclaredField(name);

    } catch (NoSuchFieldException e) {
      if (log) CarpetExtraExtrasServer.LOGGER.error("Field '{}' does not exist in object: {}", name, c);
    }

    return null;
  }
}