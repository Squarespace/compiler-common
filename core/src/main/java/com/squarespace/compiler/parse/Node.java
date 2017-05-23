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
