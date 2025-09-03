package net.thedustbuster.adaptors.carpet;

import net.thedustbuster.util.Logger;
import net.thedustbuster.util.func.Attempt;
import net.thedustbuster.util.func.option.Option;

import java.lang.reflect.Field;

public final class FieldHelper {
  public static Option<Field> getField(Class<?> c, String name) {
    return readField(c, name);
  }

  public static Option<Field> getField(Class<?> c, String n, boolean fa) {
    return getField(c, n, fa, false);
  }

  public static Option<Field> getField(Class<?> c, String name, boolean forceAccessible, boolean log) {
    return readField(c, name, log)
      .map(field -> {
        if (forceAccessible) field.setAccessible(true);
        return field;
      });
  }


  private static Option<Field> readField(Class<?> c, String n) {
    return readField(c, n, false);
  }

  private static Option<Field> readField(Class<?> c, String name, boolean log) {
    return Attempt.create(() -> Option.of(c.getDeclaredField(name)))
      .getOrHandle(e -> {
        if (log) Logger.error("Field " + name + " does not exist in class: " + c.getName());
        return Option.empty();
      });
  }
}