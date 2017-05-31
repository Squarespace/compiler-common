/**
 * Copyright (c) 2017 Squarespace, Inc.
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

package com.squarespace.compiler.match;

import static com.squarespace.compiler.common.Converters.toInteger;
import static com.squarespace.compiler.match.NodeType.EXPR;
import static com.squarespace.compiler.match.NodeType.INTEGER;
import static com.squarespace.compiler.match.NodeType.RELOP;
import static com.squarespace.compiler.match.Recognizers.characters;
import static com.squarespace.compiler.match.Recognizers.choice;
import static com.squarespace.compiler.match.Recognizers.digits;
import static com.squarespace.compiler.match.Recognizers.literal;
import static com.squarespace.compiler.match.Recognizers.sequence;
import static com.squarespace.compiler.match.Recognizers.whitespace;
import static com.squarespace.compiler.match.Recognizers.zeroOrMore;
import static com.squarespace.compiler.parse.Atom.atom;
import static com.squarespace.compiler.parse.Parser.matcher;
import static com.squarespace.compiler.parse.Struct.struct;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import com.squarespace.compiler.common.Maybe;
import com.squarespace.compiler.match.Recognizers.Recognizer;
import com.squarespace.compiler.parse.Node;
import com.squarespace.compiler.parse.Pair;
import com.squarespace.compiler.parse.Parser;

/**
 * Measure the overhead of parsing versus simply recognizing a pattern.
 */
@Fork(1)
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 5, time = 5)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
public class ParserBenchmark {

  private static final String EXPRESSION = "n % 1000 != 350";

  @Benchmark
  public void recognizer(BenchmarkState state, Blackhole blackhole) {
    blackhole.consume(state.match(EXPRESSION));
  }

  @Benchmark
  public void parser(BenchmarkState state, Blackhole blackhole) {
    blackhole.consume(state.parse(EXPRESSION));
  }

  @State(Scope.Benchmark)
  public static class BenchmarkState {

    // Assemble the expression recognizer
    private static final Recognizer M_SPACES =
        zeroOrMore(whitespace());

    private static final Recognizer M_DIGITS =
        digits();

    private static final Recognizer M_MODOP =
        sequence(characters('%'), M_SPACES, M_DIGITS);

    private static final Recognizer M_RELOP =
        choice(characters('='), literal("!="));

    private static final Recognizer M_OPERAND =
        choice(characters('n', 'i', 'v', 'w', 'f', 't'));

    private static final Recognizer M_EXPR = sequence(
        M_SPACES,
        M_OPERAND,
        M_SPACES,
        zeroOrMore(M_MODOP),
        M_SPACES,
        M_RELOP,
        M_SPACES,
        M_DIGITS);

    // Assemble the monadic expression parser
    private static Parser<CharSequence> P_SPACES =
        matcher(M_SPACES);

    private static Parser<Node<NodeType>> P_MODOP =
        matcher(characters('%')).prefix(P_SPACES).flatMap(o -> matcher(M_DIGITS).prefix(P_SPACES)
            .map(v -> atom(NodeType.MODOP, toInteger(v))));

    private static Parser<Node<NodeType>> P_RELOP =
        matcher(M_RELOP).prefix(P_SPACES)
            .map(v -> atom(RELOP, v));

    private static final Parser<Node<NodeType>> P_OPERAND =
        matcher(M_OPERAND).prefix(P_SPACES)
            .map(v -> atom(NodeType.OPERAND, v));

    private static final Parser<Node<NodeType>> P_INTEGER =
        matcher(M_DIGITS).prefix(P_SPACES)
            .map(v -> atom(INTEGER, toInteger(v)));

    private static final Parser<Node<NodeType>> P_EXPR =
        P_OPERAND.flatMap(o -> P_MODOP.orDefault(null).flatMap(m -> P_RELOP.flatMap(op -> P_INTEGER
            .map(r -> m == null ? struct(EXPR, o, op, r) : struct(EXPR, o, m, op, r)))));

    public int match(String source) {
      return M_EXPR.match(source, 0, source.length());
    }

    public Maybe<Pair<Node<NodeType>, CharSequence>> parse(String source) {
      return P_EXPR.parse(source);
    }

  }

//  public static void main(String[] args) {
//    BenchmarkState state = new BenchmarkState();
//    System.out.println(state.match(EXPRESSION));
//
//    Maybe<Pair<Node<NodeType>, CharSequence>> result = state.parse(EXPRESSION);
//    if (result.isJust()) {
//      System.out.println(result.get()._1);
//    }
//  }

}
