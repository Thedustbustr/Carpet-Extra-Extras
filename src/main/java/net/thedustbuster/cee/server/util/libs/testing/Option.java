package net.thedustbuster.cee.server.util.libs.testing;

import static net.thedustbuster.libs.func.option.None.None;

public final class Option {
  public static <T> net.thedustbuster.libs.func.option.Option<T> asType(Object obj, Class<T> type) {
    return type.isInstance(obj) ? net.thedustbuster.libs.func.option.Option.of(type.cast(obj)) : None();
  }
}
