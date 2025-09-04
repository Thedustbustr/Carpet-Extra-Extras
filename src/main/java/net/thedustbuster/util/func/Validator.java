package net.thedustbuster.util.func;

import net.thedustbuster.util.func.option.None;
import net.thedustbuster.util.func.option.Option;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.*;
import java.util.stream.Stream;

import static net.thedustbuster.util.func.option.None.None;

public final class Validator<T, B> {
  private final T raw;
  private final Option<B> builderOpt;
  private final List<String> errors = new ArrayList<>();

  private Validator(T raw, Option<B> builderOpt) {
    this.raw = raw;
    this.builderOpt = builderOpt;
  }

  public static <T, B> Validator<T, B> of(T raw, B builder) {
    return new Validator<>(raw, Option.of(builder));
  }

  public static <T> Validator<T, Void> of(T raw) {
    return new Validator<>(raw, None());
  }

  public boolean hasBuilder() {
    return builderOpt.isDefined();
  }

  public Either<B, List<String>> toEither() {
    return errors.isEmpty() ? Either.left(builderOrThrow()) : Either.right(getErrorsList());
  }

  public Either<B, List<String>> toEitherFormatted() {
    return errors.isEmpty() ? Either.left(builderOrThrow()) : Either.right(getErrors());
  }

  public List<String> getErrorsList() {
    return List.copyOf(errors);
  }

  public List<String> getErrors() {
    if (errors.isEmpty()) return Collections.emptyList();

    StringBuilder sb = new StringBuilder();
    errors.forEach(e -> {
      if (e.contains("\n")) {
        Stream.of(e.split("\n"))
          .filter(line -> !line.isBlank())
          .forEach(line -> sb.append("  ").append(line).append("\n"));
      } else {
        sb.append("  ").append(e).append("\n");
      }
    });
    return List.of(sb.toString());
  }

  // === Helpers ===
  private B builderOrThrow() {
    return builderOpt.getOrThrow(() -> new IllegalStateException("Builder is required"));
  }

  private Validator<T, B> recordError(Supplier<String> errorSupplier) {
    errors.add(errorSupplier.get());
    return this;
  }

  private Validator<T, B> recordIfFalse(boolean cond, Supplier<String> errorSupplier) {
    if (!cond) errors.add(errorSupplier.get());
    return this;
  }

  // === Basic check/apply ===
  public Validator<T, B> check(boolean cond, String error) {
    return recordIfFalse(cond, () -> error);
  }

  public Validator<T, B> check(boolean cond, Supplier<String> errorMessageFn) {
    return recordIfFalse(cond, errorMessageFn);
  }

  public Validator<T, B> checkAndApply(boolean cond, Supplier<String> errorMessageFn, Consumer<B> onSuccess) {
    if (!cond) return recordError(errorMessageFn);
    onSuccess.accept(builderOrThrow());
    return this;
  }

  // === Basic require/apply ===
  public Validator<T, B> require(boolean cond, String error) {
    return recordIfFalse(cond, () -> error);
  }

  public Validator<T, B> require(boolean cond, Supplier<String> errorMessageFn) {
    return recordIfFalse(cond, errorMessageFn);
  }

  public Validator<T, B> requireAndApply(boolean cond, Supplier<String> errorMessageFn, Consumer<B> onSuccess) {
    if (!cond) return recordError(errorMessageFn);
    onSuccess.accept(builderOrThrow());
    return this;
  }

  // === Option methods ===
  public <U> Validator<T, B> requiredOption(Option<U> maybe, String errorIfMissing) {
    return recordIfFalse(maybe.isDefined(), () -> errorIfMissing);
  }

  public <U> Validator<T, B> requireAndApplyOption(Option<U> maybe, Supplier<String> errorIfMissing, BiConsumer<B, U> onSuccess) {
    if (maybe.isEmpty()) return recordError(errorIfMissing);
    onSuccess.accept(builderOrThrow(), maybe.get());
    return this;
  }

  public <U, R> Validator<T, B> requireAndApplyOption(Option<U> maybe, Function<U, Option<R>> extractor, Function<Option<U>, String> errorMessageFn, String errorIfMissing, BiConsumer<B, R> onSuccess) {
    if (maybe.isEmpty()) return recordError(() -> errorIfMissing);
    Option<R> result = extractor.apply(maybe.get());
    if (result.isEmpty()) return recordError(() -> errorMessageFn.apply(maybe));
    onSuccess.accept(builderOrThrow(), result.get());
    return this;
  }

  public <U, R> Validator<T, B> requireOptionMap(Option<? extends Collection<U>> maybeValues, Function<U, Option<R>> extractor, BiFunction<U, Integer, String> errorMessageFn, Supplier<String> errorIfMissing, BiConsumer<B, R> onSuccess) {
    if (maybeValues.isEmpty()) return recordError(errorIfMissing);

    maybeValues.whenDefined(coll -> {
      int i = 0;
      for (U u : coll) {
        Option<R> result = extractor.apply(u);
        if (result.isDefined()) onSuccess.accept(builderOrThrow(), result.get());
        else errors.add(errorMessageFn.apply(u, i));
        i++;
      }
    });
    return this;
  }

  public <U> Validator<T, B> passOption(Option<U> maybe, BiConsumer<B, Option<U>> onSuccess) {
    onSuccess.accept(builderOrThrow(), maybe);
    return this;
  }

  public <U> Validator<T, B> checkOption(Option<U> maybe, Predicate<U> pred, Supplier<String> errorMessageFn) {
    maybe.whenDefined(u -> {
      if (!pred.test(u)) errors.add(errorMessageFn.get());
    });
    return this;
  }

  public <U> Validator<T, B> checkAndApplyOption(Option<U> maybe, BiConsumer<B, U> onSuccess) {
    maybe.whenDefined(o -> onSuccess.accept(builderOrThrow(), o));
    return this;
  }


  public <U, R> Validator<T, B> checkAndApplyOption(Option<U> maybe, Function<U, Option<R>> extractor, Function<U, String> errorMessageFn, BiConsumer<B, Option<R>> onSuccess) {
    Option<R> result = maybe.flatMap(extractor);
    if (maybe.isDefined() && result.isEmpty()) errors.add(errorMessageFn.apply(maybe.get()));
    builderOpt.whenDefined(b -> onSuccess.accept(b, maybe.fold(__ -> result, None::None)));
    return this;
  }

  // === Collection / Map-like methods ===
  public <U> Validator<T, B> checkForEach(Collection<U> values, BiConsumer<U, Validator<T, B>> fn) {
    values.forEach(u -> fn.accept(u, this));
    return this;
  }

  public <U> Validator<T, B> checkOptionForEach(Option<? extends Collection<U>> maybeValues, BiConsumer<U, Validator<T, B>> fn) {
    maybeValues.whenDefined(coll -> checkForEach(coll, fn));
    return this;
  }

  public <U, R> Validator<T, B> checkOptionMap(Option<? extends Collection<U>> maybeValues, Function<U, Option<R>> extractor, BiFunction<U, Integer, String> errorMessageFn, BiConsumer<B, R> onSuccess) {
    maybeValues.whenDefined(coll -> {
      int i = 0;
      for (U u : coll) {
        Option<R> result = extractor.apply(u);
        if (result.isDefined()) onSuccess.accept(builderOrThrow(), result.get());
        else errors.add(errorMessageFn.apply(u, i));
        i++;
      }
    });
    return this;
  }

  // === Nested validators ===
  public <U, B2> Validator<T, B> validate(U u, Function<U, Validator<U, B2>> nestedValidator) {
    this.errors.addAll(nestedValidator.apply(u).getErrors());
    return this;
  }

  public <U, B2> Validator<T, B> validateAndApply(U u, Function<U, Validator<U, B2>> nestedValidator, BiConsumer<B2, B> onSuccess) {
    Validator<U, B2> nested = nestedValidator.apply(u);

    if (nested.getErrors().isEmpty()) {
      onSuccess.accept(nested.builderOrThrow(), builderOrThrow());
    } else {
      this.errors.addAll(nested.getErrors());
    }

    return this;
  }

  public <U, B2> Validator<T, B> validateOption(Option<U> maybeValue, Function<U, Validator<U, B2>> nestedValidator, Function<U, String> errorMessageFn, BiConsumer<B2, B> onSuccess) {
    maybeValue.whenDefined(value -> {
        Validator<U, B2> nested = nestedValidator.apply(value);

        if (nested.getErrors().isEmpty()) {
          onSuccess.accept(nested.builderOrThrow(), builderOrThrow());
        } else {
          errorMessageFn.apply(maybeValue.get());
          this.errors.addAll(nested.getErrors());
        }
      }
    );

    return this;
  }

  public <U, B2> Validator<T, B> requireValidateOption(Option<U> maybeValue, Function<U, Validator<U, B2>> nestedValidator, BiConsumer<B2, B> onSuccess, Supplier<String> errorIfMissing) {
    if (maybeValue.isEmpty()) return recordError(errorIfMissing);

    Validator<U, B2> nested = nestedValidator.apply(maybeValue.get());
    if (nested.getErrors().isEmpty()) {
      onSuccess.accept(nested.builderOrThrow(), builderOrThrow());
    } else {
      this.errors.addAll(nested.getErrors());
    }

    return this;
  }

  public <U, B2> Validator<T, B> validateOptionMap(Option<? extends Collection<U>> maybeValues, Function<U, Validator<U, B2>> nestedValidator, BiFunction<U, Integer, String> errorMessageFn, BiConsumer<B2, B> onSuccess) {
    maybeValues.whenDefined(coll -> {
      int i = 0;
      for (U u : coll) {
        Validator<U, B2> nested = nestedValidator.apply(u);
        List<String> nestedErrors = nested.getErrors();

        if (nestedErrors.isEmpty()) {
          onSuccess.accept(nested.builderOrThrow(), builderOrThrow());
        } else {
          this.errors.add(errorMessageFn.apply(u, i));
          this.errors.addAll(nestedErrors);
        }
        i++;
      }
    });

    return this;
  }

  public <U, B2> Validator<T, B> requireValidateOptionMap(Option<? extends Collection<U>> maybeValues, Function<U, Validator<U, B2>> nestedValidator, BiFunction<U, Integer, String> errorMessageFn, String errorIfMissing, BiConsumer<B2, B> onSuccess) {
    if (maybeValues.isEmpty()) return recordError(() -> errorIfMissing);

    maybeValues.whenDefined(coll -> {
      int i = 0;
      for (U u : coll) {
        Validator<U, B2> nested = nestedValidator.apply(u);
        List<String> nestedErrors = nested.getErrors();

        if (nestedErrors.isEmpty()) {
          onSuccess.accept(nested.builderOrThrow(), builderOrThrow());
        } else {
          this.errors.add(errorMessageFn.apply(u, i));
          this.errors.addAll(nestedErrors);
        }
        i++;
      }
    });

    return this;
  }
}