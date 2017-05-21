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
