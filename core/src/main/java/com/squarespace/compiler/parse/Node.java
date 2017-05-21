package com.squarespace.compiler.parse;


/**
 * Base for atoms and tuples.
 */
public abstract class Node<T extends Enum<T>> {

  private final T type;

  public Node(T type) {
    this.type = type;
  }

  public T type() {
    return type;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    Printer.print(this, buf);
    return buf.toString();
  }

  public Struct<T> asStruct() {
    return (Struct<T>) this;
  }

  public Atom<T> asAtom() {
    return (Atom<T>) this;
  }

}
