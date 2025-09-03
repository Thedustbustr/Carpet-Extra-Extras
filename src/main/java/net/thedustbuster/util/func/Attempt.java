package net.thedustbuster.util.func;

import net.thedustbuster.util.func.checked.CheckedFunction;
import net.thedustbuster.util.func.checked.CheckedRunnable;
import net.thedustbuster.util.func.checked.CheckedSupplier;
import net.thedustbuster.util.func.option.Option;

import java.util.function.Function;

import static net.thedustbuster.util.func.Unit.Unit;
import static net.thedustbuster.util.func.option.None.None;

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

  public static Attempt<Unit> run(CheckedRunnable fn) {
    try {
      fn.run();
      return new Attempt<>(Unit);
    } catch (Exception e) {
      return new Attempt<>(e);
    }
  }

  // Constructors //
  public Attempt(T value) {
    this.value = Option.of(value);
    this.exception = None();
  }

  public Attempt(Exception e) {
    this.value = None();
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

  public Option<T> toOption() {
    if (exception.isDefined()) return None();
    return value;
  }

  public Attempt<T> handleException(CheckedFunction<Exception, T> handler) {
    return exception.map(e -> Attempt.create(handler, e)).orElse(this);
  }

  public T getOrHandle(CheckedFunction<Exception, T> handler) {
    return handleException(handler).get();
  }

  public T orThrow(Function<Exception, RuntimeException> handler) {
    return value.getOrThrow(() -> handler.apply(exception.getOrElse(new RuntimeException("Unexpected error: no value and no exception"))));
  }

  public T get() {
    return value.getOrThrow(() -> exception
      // If the exception is a RuntimeException, cast it, otherwise create a new RuntimeException
      .map(e -> e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e))
      .orElse(new RuntimeException("Unexpected error: no value and no exception")));
  }
}