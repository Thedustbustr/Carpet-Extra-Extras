package net.thedustbuster.util.func.option;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static net.thedustbuster.util.func.option.None.None;
import static net.thedustbuster.util.func.option.Some.Some;

public abstract class Option<T> {
  public static <T> Option<T> of(T value) {
    return value != null && !(value instanceof None) ? Some(value) : None();
  }

  public static <T> Option<T> of(Optional<T> optional) {
    return optional != null && optional.isPresent() ? Some(optional.get()) : None();
  }

  public static <T> Option<T> safe(Option<T> option) { return option == null ? None() : option; }

  public static <T> Option<T> empty() { return None(); }

  public abstract T get();

  public abstract T orElse(T other);

  public abstract T getOrElse(T defaultValue);

  public abstract T orElseGet(Supplier<? extends T> supplier);

  public abstract <E extends Throwable> T getOrThrow(Supplier<? extends E> exceptionSupplier) throws E;

  public abstract <U> U fold(Function<T, U> mapper, Supplier<U> defaultValue);

  public abstract Option<T> whenDefined(Consumer<T> consumer);

  public abstract Option<T> whenDefined(Runnable runnable);

  public abstract boolean isDefined();

  public abstract void isDefinedOrElse(Consumer<? super T> consumer, Runnable runnable);

  public abstract Option<T> whenEmpty(Runnable runnable);

  public abstract boolean isEmpty();

  public abstract <U> Option<U> map(Function<? super T, ? extends U> mapper);

  public abstract <U> Option<U> flatMap(Function<? super T, Option<U>> mapper);

  public abstract Option<T> filter(Predicate<? super T> predicate);
}
