package com.squarespace.compiler.common;

import java.util.NoSuchElementException;


/**
 * Represents absence of a value in the Maybe monad.
 */
public class Nothing<T> implements Maybe<T> {

  static final Maybe<?> NOTHING = new Nothing<>();

  @Override
  public boolean isJust() {
    return false;
  }

  @Override
  public T get() {
    throw new NoSuchElementException("Nothing.get() is not valid");
  }

}
