package net.thedustbuster.util.libs.testing;

import static net.thedustbuster.libs.func.option.None.None;
import static net.thedustbuster.libs.func.option.Some.Some;

public final class Option {
  public static <T> net.thedustbuster.libs.func.option.Option<T> asType(Object obj, Class<T> type) {
    return type.isInstance(obj) ? Some(type.cast(obj)) : None();
  }
}
