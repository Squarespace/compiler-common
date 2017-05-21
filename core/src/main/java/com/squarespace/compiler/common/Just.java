package com.squarespace.compiler.common;


/**
 * Represents a value in the Maybe monad.
 */
public class Just<T> implements Maybe<T> {

  private final T value;

  public Just(T value) {
    this.value = value;
  }

  @Override
  public T get() {
    return value;
  }

  @Override
  public boolean isJust() {
    return true;
  }

}
