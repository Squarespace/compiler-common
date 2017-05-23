/**
 * Copyright, 2017, Squarespace, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
