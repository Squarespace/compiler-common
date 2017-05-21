package com.squarespace.compiler.parse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


/**
 * Generic nested structure.
 */
public class Struct<T extends Enum<T>> extends Node<T> {

  private List<Node<T>> nodes;

  public Struct(T type) {
    super(type);
    this.nodes = new ArrayList<>();
  }

  public Struct(T type, List<? extends Node<T>> nodes) {
    this(type);
    this.nodes.addAll(nodes);
  }

  @SafeVarargs
  public <N extends Node<T>> Struct(T type, N... nodes) {
    this(type, Arrays.asList(nodes));
  }

  public void add(Node<T> node) {
    this.nodes.add(node);
  }

  @SafeVarargs
  public final Struct<T> addNotNull(Node<T>... nodes) {
    for (Node<T> n : nodes) {
      if (n != null) {
        this.nodes.add(n);
      }
    }
    return this;
  }

  public List<Node<T>> nodes() {
    return nodes;
  }

  @SafeVarargs
  public static <T extends Enum<T>> Node<T> struct(T type, Node<T>... nodes) {
    return new Struct<>(type, Arrays.asList(nodes));
  }

  public static <T extends Enum<T>> Node<T> struct(T type, List<? extends Node<T>> nodes) {
    return new Struct<>(type, nodes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type(), nodes);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Struct) {
      Struct<?> other = (Struct<?>) obj;
      return type().equals(other.type()) && nodes.equals(other.nodes);
    }
    return false;
  }

}
