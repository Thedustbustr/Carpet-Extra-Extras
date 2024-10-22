package net.thedustbuster.util;

import net.thedustbuster.util.checked.CheckedFunction;
import net.thedustbuster.util.checked.CheckedSupplier;
import net.thedustbuster.util.option.None;
import net.thedustbuster.util.option.Option;

import java.util.function.Function;

public class Attempt<T> {
  private final Option<T> value;
  private final Option<Exception> exception;

  public static <R, S> Attempt<S> create(CheckedFunction<R, S> fn, R argument) {
    return create(() -> fn.apply(argument));
  }

  public static <S> Attempt<S> create(CheckedSupplier<S> fn) {
    try {
      return new Attempt<S>(fn.get());
    } catch (Exception e) {
      return new Attempt<S>(e);
    }
  }

  // Constructors //
  public Attempt(T value) {
    this.value = Option.of(value);
    this.exception = new None<>();
  }

  public Attempt(Exception e) {
    this.value = new None<>();
    this.exception = Option.of(e);
  }

  @SuppressWarnings("unchecked")
  public <S> Attempt<S> map(CheckedFunction<T, S> fn) {
    return value.map(t -> Attempt.create(fn, t)).orElseGet(() -> (Attempt<S>) this);
  }

  @SuppressWarnings("unchecked")
  public <S> Attempt<S> flatMap(Function<T, Attempt<S>> fn) {
    return value.map(fn).orElseGet(() -> (Attempt<S>) this);
  }

  @SuppressWarnings("unchecked")
  public <S> Attempt<S> flatMapError(Function<Exception, Attempt<S>> handler) {
    return exception.map(handler).orElseGet(() -> (Attempt<S>) this);
  }

  public Attempt<T> handleException(CheckedFunction<Exception, T> handler) {
    return exception.map(e -> Attempt.create(handler, e)).orElse(this);
  }

  public T getOrHandle(CheckedFunction<Exception, T> handler) {
    return handleException(handler).get();
  }

  public T get() {
    return value.orElseThrow(() -> exception
      // If the exception is a RuntimeException, cast it, otherwise create a new RuntimeException
      .map(e -> e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e))
      .orElse(new RuntimeException("Unexpected error: no value and no exception")));
  }
}