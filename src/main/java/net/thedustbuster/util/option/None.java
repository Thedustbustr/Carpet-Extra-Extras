package net.thedustbuster.util.option;

import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Supplier;

public final class None<T> extends Option<T> {
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
  public <E extends Throwable> T orElseThrow(Supplier<? extends E> exceptionSupplier) throws E {
    throw exceptionSupplier.get();
  }

  @Override
  public <U> U fold(Function<T, U> mapper, Supplier<U> defaultValue) {
    return defaultValue.get();
  }

  @Override
  public boolean isDefined() {
    return false;
  }

  @Override
  public boolean isEmpty() {
    return true;
  }

  @Override
  public <U> Option<U> map(Function<? super T, ? extends U> mapper) {
    return new None<>();
  }

  @Override
  public <U> Option<U> flatMap(Function<? super T, Option<U>> mapper) {
    return new None<>();
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