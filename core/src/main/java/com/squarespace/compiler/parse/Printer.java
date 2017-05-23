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

import java.util.List;


public class Printer {

  private static final int INCR = 2;

  public static <T extends Enum<T>> void print(Node<T> node, StringBuilder buf) {
    print(node, buf, 0);
  }

  private static <T extends Enum<T>> void print(Node<T> node, StringBuilder buf, int depth) {
    buf.append('(').append(node.type()).append(' ');
    if (node instanceof Struct<?>) {
      depth++;
      buf.append('\n');

      Struct<T> tuple = (Struct<T>) node;
      List<Node<T>> nodes = tuple.nodes();
      int size = nodes.size();
      for (int i = 0; i < size; i++) {
        indent(buf, depth);
        print(nodes.get(i), buf, depth);
        buf.append('\n');
      }

      depth--;
      indent(buf, depth);

    } else {
      Atom<?> atom = (Atom<?>) node;
      buf.append(atom.value());
    }

    buf.append(')');
  }

  private static void indent(StringBuilder buf, int size) {
    for (int i = 0; i < size * INCR; i++) {
      buf.append(' ');
    }
  }

}
