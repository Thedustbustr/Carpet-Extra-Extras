package net.thedustbuster.util.option;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Some<T> extends Option<T> {
  private final T value;

  public Some(@NotNull T value) {
    this.value = Objects.requireNonNull(value, "Value cannot be null");
  }

  @Override
  public T get() {
    return value;
  }

  @Override
  public T orElse(T other) {
    return value;
  }

  @Override
  public T getOrElse(T defaultValue) {
    return value;
  }

  @Override
  public T orElseGet(Supplier<? extends T> supplier) {
    return value;
  }

  @Override
  public <E extends Throwable> T orElseThrow(Supplier<? extends E> exceptionSupplier) {
    return value;
  }

  @Override
  public boolean isDefined() {
    return true;
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public <U> Option<U> map(Function<? super T, ? extends U> mapper) {
    return Option.of(mapper.apply(value));
  }

  @Override
  public <U> Option<U> flatMap(Function<? super T, Option<U>> mapper) {
    return mapper.apply(value);
  }

  @Override
  public String toString() {
    return "Some(" + value + ")";
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;

    return switch (obj) {
      case Some<?> o -> Objects.equals(value, o.value);
      default -> false;
    };
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}