package com.squarespace.compiler.common;

import java.util.function.Function;
import java.util.function.Supplier;


/**
 * Maybe monad.
 */
public interface Maybe<T> {

  T get();

  boolean isJust();

  default boolean isNothing() {
    return !isJust();
  }

  @SuppressWarnings("unchecked")
  public static <T> Maybe<T> nothing() {
    return (Maybe<T>) Nothing.NOTHING;
  }

  public static <T> Maybe<T> just(T value) {
    return new Just<>(value);
  }

  default <R> Maybe<R> map(Function<T, R> f) {
    return isJust() ? just(f.apply(get())) : nothing();
  }

  default <R> Maybe<R> flatMap(Function<T, Maybe<R>> f) {
    return isJust() ? f.apply(get()) : nothing();
  }

  default Maybe<T> orElse(Supplier<Maybe<T>> f) {
    return isJust() ? this : f.get();
  }

}
