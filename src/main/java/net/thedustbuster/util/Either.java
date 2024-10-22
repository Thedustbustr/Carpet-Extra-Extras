package net.thedustbuster.util;

import net.thedustbuster.util.option.Option;

import java.util.function.Function;

public class Either<L, R> {
  private final Option<L> left;
  private final Option<R> right;

  public static <L, R> Either<L, R> right(R value) {
    return new Either<>(Option.empty(), Option.of(value));
  }

  public static <L, R> Either<L, R> left(L value) {
    return new Either<>(Option.of(value), Option.empty());
  }

  public static <L, R> Either<L, R> cond(boolean bool, L left, R right) {
    return !bool ? Either.left(left) : Either.right(right);
  }

  private Either(Option<L> left, Option<R> right) {
    this.left = left;
    this.right = right;
  }

  public boolean isRight() {
    return right.isEmpty();
  }

  public boolean isLeft() {
    return left.isEmpty();
  }

  public R getRight() {
    return right.orElseThrow(() -> new IllegalStateException("Tried to get right value from a left Either."));
  }

  public L getLeft() {
    return left.orElseThrow(() -> new IllegalStateException("Tried to get left value from a right Either."));
  }

  public <T> Either<L, T> map(Function<R, T> fn) {
    return right.<Either<L, T>>map(r -> Either.right(fn.apply(r))).orElseGet(() -> Either.left(getLeft()));
  }

  public <T> Either<L, T> flatMap(Function<R, Either<L, T>> fn) {
    return right.map(fn).orElseGet(() -> Either.left(getLeft()));
  }

  public <T> Either<T, R> mapLeft(Function<L, T> fn) {
    return left.<Either<T, R>>map(l -> Either.left(fn.apply(l))).orElseGet(() -> Either.right(getRight()));
  }

  public <T> Either<T, R> flatMapLeft(Function<L, Either<T, R>> fn) {
    return left.map(fn).orElseGet(() -> Either.right(getRight()));
  }

  public Either<L, R> handleLeft(Function<L, R> fn) {
    return left.<Either<L, R>>map(l -> Either.right(fn.apply(l))).orElse(this);
  }

  public <T> T fold(Function<L, T> leftFn, Function<R, T> rightFn) {
    if (right.isDefined()) {
      return rightFn.apply(right.get());
    } else {
      return leftFn.apply(left.get());
    }
  }
}
