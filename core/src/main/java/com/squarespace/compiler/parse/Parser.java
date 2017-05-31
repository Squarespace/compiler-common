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

  Maybe<Pair<T, CharSequence>> parse(CharSequence s);

  static Parser<CharSequence> matcher(Recognizer pattern) {
    return s -> {
      int length = s.length();
      int end = pattern.match(s, 0, length);
      return end == -1 ? nothing() : just(pair(s.subSequence(0, end), s.subSequence(end, length)));
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

  static <T> List<T> cons(T x, List<T> xs) {
    List<T> result = new ArrayList<>();
    result.add(x);
    result.addAll(xs);
    return result;
  }

}
