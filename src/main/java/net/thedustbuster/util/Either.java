package net.thedustbuster.util;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class Either<L, R> {
  private final Optional<L> left;
  private final Optional<R> right;

  public static <L, R> Either<L, R> right(R value) {
    return new Either<>(Optional.empty(), Optional.of(value));
  }

  public static <L, R> Either<L, R> left(L value) {
    return new Either<>(Optional.of(value), Optional.empty());
  }

  public static <L, R> Either<L, R> cond(boolean bool, L left, R right) {
    return !bool ? Either.left(left) : Either.right(right);
  }

  private Either(Optional<L> left, Optional<R> right) {
    this.left = left;
    this.right = right;
  }

  public boolean isRight() {
    return right.isPresent();
  }

  public boolean isLeft() {
    return left.isPresent();
  }

  public R getRight() {
    return right.orElseThrow(() -> new IllegalStateException("Tried to get right value from a left Either."));
  }

  public L getLeft() {
    return left.orElseThrow(() -> new IllegalStateException("Tried to get left value from a right Either."));
  }

  public <T> Either<L, T> map(Function<R, T> fn) {
    if (right.isPresent()) {
      return Either.right(fn.apply(right.get()));
    } else {
      return Either.left(getLeft());
    }
  }

  public <T> Either<L, T> flatMap(Function<R, Either<L, T>> fn) {
    if (right.isPresent()) {
      return fn.apply(right.get());
    } else {
      return Either.left(getLeft());
    }
  }

  public <T> Either<T, R> mapLeft(Function<L, T> fn) {
    if (left.isPresent()) {
      return Either.left(fn.apply(left.get()));
    } else {
      return Either.right(getRight());
    }
  }

  public <T> Either<T, R> flatMapLeft(Function<L, Either<T, R>> fn) {
    if (left.isPresent()) {
      return fn.apply(left.get());
    } else {
      return Either.right(getRight());
    }
  }

  public Either<L, R> handleLeft(Function<L, R> fn) {
    if (left.isPresent()) {
      return Either.right(fn.apply(left.get()));
    } else {
      return this;
    }
  }

  public Either<L, R> ifRight(Consumer<R> consumer) {
    right.ifPresent(consumer);
    return this;
  }

  public Either<L, R> ifLeft(Consumer<L> consumer) {
    left.ifPresent(consumer);
    return this;
  }

  public <T> T fold(Function<L, T> leftFn, Function<R, T> rightFn) {
    if (right.isPresent()) {
      return rightFn.apply(right.get());
    } else {
      return leftFn.apply(left.get());
    }
  }
}
