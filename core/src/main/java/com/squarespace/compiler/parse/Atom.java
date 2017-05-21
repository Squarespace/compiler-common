package com.squarespace.compiler.parse;

import java.util.Objects;


/**
 * Wrapper for an atomic value.
 */
public class Atom<T extends Enum<T>> extends Node<T> {

  private final Object value;

  private final int hashCode;

  public Atom(T type, Object value) {
    super(type);
    this.value = value;
    this.hashCode = Objects.hash(type, value);
  }

  public Object value() {
    return value;
  }

  public static <T extends Enum<T>> Node<T> atom(T type, Object value) {
    return new Atom<T>(type, value);
  }

  @Override
  public int hashCode() {
    return hashCode;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Atom) {
      Atom<?> other = (Atom<?>) obj;
      return type().equals(other.type()) && Objects.equals(value, other.value);
    }
    return false;
  }

}
