package com.squarespace.compiler.parse;

import static com.squarespace.compiler.common.Maybe.just;
import static com.squarespace.compiler.common.Maybe.nothing;
import static com.squarespace.compiler.parse.Pair.pair;
import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.squarespace.compiler.common.Maybe;
import com.squarespace.compiler.match.Recognizers.Recognizer;


/**
 * Monadic parser.
 */
public interface Parser<T> {

  Maybe<Pair<T, String>> parse(String s);

  static Parser<String> matcher(Recognizer pattern) {
    return s -> {
      int end = pattern.match(s, 0, s.length());
      return end == -1 ? nothing() : just(pair(s.substring(0, end), s.substring(end)));
    };
  }

  default <R> Parser<T> prefix(Parser<R> parser) {
    return parser.flatMap(o -> this);
  }

  default Parser<T> suffix(Parser<?> parser) {
    return this.flatMap(t -> parser.map(o -> t));
  }

  default <R> Parser<R> map(Function<T, R> f) {
    return s -> parse(s).map(p -> pair(f.apply(p._1), p._2));
}

  default <R> Parser<R> flatMap(Function<T, Parser<R>> f) {
    return s -> parse(s).flatMap(p -> f.apply(p._1).parse(p._2));
  }

  default Parser<List<T>> zeroOrMore() {
    return oneOrMore().orDefault(emptyList());
  }

  default Parser<List<T>> oneOrMore() {
    return flatMap(x -> zeroOrMore().map(xs -> cons(x, xs)));
  }

  default Parser<T> or(Parser<T> alt) {
    return s -> parse(s).orElse(() -> alt.parse(s));
  }

  default Parser<T> orDefault(T v) {
    return s -> parse(s).orElse(() -> just(pair(v, s)));
  }

  default <R> Parser<List<T>> separated(Parser<R> delimiter) {
    Parser<T> skipped = prefix(delimiter);
    return flatMap(t -> skipped.zeroOrMore().map(ts -> cons(t, ts)));
  }

  public static <T> List<T> cons(T x, List<T> xs) {
    List<T> result = new ArrayList<>();
    result.add(x);
    result.addAll(xs);
    return result;
  }

}
