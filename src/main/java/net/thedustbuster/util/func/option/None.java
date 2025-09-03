package net.thedustbuster.util.func.option;

import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class None<T> extends Option<T> {
  private None() { }
  private static final Option<?> None = new None<>();

  @SuppressWarnings("unchecked")
  public static <T> Option<T> None() { return (Option<T>) None; }

  @Override
  public T get() {
    throw new NoSuchElementException("No value present");
  }

  @Override
  public T orElse(T other) {
    return other;
  }

  @Override
  public T getOrElse(T defaultValue) {
    return defaultValue;
  }

  @Override
  public T orElseGet(Supplier<? extends T> supplier) {
    return supplier.get();
  }

  @Override
  public <E extends Throwable> T getOrThrow(Supplier<? extends E> exceptionSupplier) throws E {
    throw exceptionSupplier.get();
  }

  @Override
  public <U> U fold(Function<T, U> mapper, Supplier<U> defaultValue) {
    return defaultValue.get();
  }

  @Override
  public Option<T> whenDefined(Consumer<T> consumer) {
    return this;
  }

  @Override
  public Option<T> whenDefined(Runnable runnable) { return this; }

  @Override
  public boolean isDefined() {
    return false;
  }

  @Override
  public void isDefinedOrElse(Consumer<? super T> consumer, Runnable runnable) { runnable.run(); }

  @Override
  public Option<T> whenEmpty(Runnable runnable) {
    runnable.run();
    return this;
  }

  @Override
  public boolean isEmpty() {
    return true;
  }

  @Override
  public <U> Option<U> map(Function<? super T, ? extends U> mapper) {
    return None();
  }

  @Override
  public <U> Option<U> flatMap(Function<? super T, Option<U>> mapper) {
    return None();
  }

  @Override
  public Option<T> filter(Predicate<? super T> predicate) {
    return this;
  }

  @Override
  public String toString() {
    return "None";
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof None;
  }

  @Override
  public int hashCode() {
    return 0;
  }
}