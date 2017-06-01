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

import static com.squarespace.compiler.match.Recognizers.charClass;
import static com.squarespace.compiler.match.Recognizers.characters;
import static com.squarespace.compiler.match.Recognizers.choice;
import static com.squarespace.compiler.match.Recognizers.digit;
import static com.squarespace.compiler.match.Recognizers.digits;
import static com.squarespace.compiler.match.Recognizers.literal;
import static com.squarespace.compiler.match.Recognizers.oneOrMore;
import static com.squarespace.compiler.match.Recognizers.whitespace;
import static com.squarespace.compiler.match.Recognizers.zeroOrMore;
import static com.squarespace.compiler.parse.Atom.atom;
import static com.squarespace.compiler.parse.Parser.cons;
import static com.squarespace.compiler.parse.Parser.matcher;
import static com.squarespace.compiler.parse.ParserTest.TestType.CHOICES;
import static com.squarespace.compiler.parse.ParserTest.TestType.EXPR;
import static com.squarespace.compiler.parse.ParserTest.TestType.INTEGER;
import static com.squarespace.compiler.parse.ParserTest.TestType.INTLIST;
import static com.squarespace.compiler.parse.ParserTest.TestType.LITERAL;
import static com.squarespace.compiler.parse.ParserTest.TestType.OP;
import static com.squarespace.compiler.parse.ParserTest.TestType.VAR;
import static com.squarespace.compiler.parse.Struct.struct;
import static com.squarespace.compiler.text.CharClass.LOWERCASE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import com.squarespace.compiler.common.Maybe;


public class ParserTest {

  @Test
  public void testMany() {
    Maybe<Pair<List<CharSequence>, CharSequence>> r = matcher(digit()).zeroOrMore().parse("123");
    assertEquals(r.get()._1, Arrays.asList("1", "2", "3"));

    r = matcher(digit()).oneOrMore().parse("123");
    assertEquals(r.get()._1, Arrays.asList("1", "2", "3"));

    r = matcher(digit()).oneOrMore().parse("");
    assertFalse(r.isJust());

    r = matcher(digit()).zeroOrMore().parse("");
    assertTrue(r.isJust());

    Maybe<Pair<CharSequence, CharSequence>> r2 = matcher(digits()).parse("123");
    assertEquals(r2.get()._1, "123");
  }

  @Test
  public void testCons() {
    assertEquals(cons("a", Arrays.asList("b", "c")), Arrays.asList("a", "b", "c"));
  }

  @Test
  public void testTypedParser() {
    Maybe<Pair<Node<TestType>, CharSequence>> r = P_VAR.parse(" num");
    assertTrue(r.isJust());
    assertEquals(r.get()._1, atom(VAR, "num"));

    r = P_EXPR.parse("number = 123");
    assertTrue(r.isJust());
    assertEquals(r.get()._1,
        struct(EXPR,
            atom(VAR, "number"),
            atom(OP, "="),
            struct(INTLIST,
                atom(INTEGER, 123))));

    r = P_BLOCK.parse(" { number != 123, 456, 789 } ");
    assertTrue(r.isJust());
    assertEquals(r.get()._1,
        struct(EXPR,
            atom(VAR, "number"),
            atom(OP, "!="),
            struct(INTLIST,
                atom(INTEGER, 123),
                atom(INTEGER, 456),
                atom(INTEGER, 789))));

    r = P_INTLIST.parse(" 1, 2 , 3,\n4, 5");
    assertTrue(r.isJust());
    assertEquals(r.get()._1,
        struct(INTLIST,
            atom(INTEGER, 1),
            atom(INTEGER, 2),
            atom(INTEGER, 3),
            atom(INTEGER, 4),
            atom(INTEGER, 5)));

    r = P_CHOICES.parse("  def   abc  def   ghi");
    assertTrue(r.isJust());
    assertEquals(r.get()._1,
        struct(CHOICES,
            atom(LITERAL, "def"),
            atom(LITERAL, "abc"),
            atom(LITERAL, "def"),
            atom(LITERAL, "ghi")));
  }

  private final Parser<CharSequence> P_SPACE =
      matcher(zeroOrMore(whitespace()));

  private final Parser<Node<TestType>> P_VAR =
      matcher(oneOrMore(charClass(LOWERCASE))).prefix(P_SPACE)
          .map(v -> atom(VAR, v));

  private final Parser<Node<TestType>> P_OP =
      matcher(choice(literal("="), literal("!="))).prefix(P_SPACE)
          .map(v -> atom(OP, v));

  private final Parser<Node<TestType>> P_INTEGER =
      matcher(digits()).prefix(P_SPACE)
          .map(v -> atom(INTEGER, Integer.valueOf(v.toString())));

  private final Parser<CharSequence> P_COMMA =
      matcher(characters(',')).prefix(P_SPACE);

  private final Parser<Node<TestType>> P_INTLIST =
      P_INTEGER.separated(P_COMMA)
          .map(i -> struct(TestType.INTLIST, i));

  private final Parser<Node<TestType>> P_EXPR =
      P_VAR.flatMap(v -> P_OP.flatMap(o -> P_INTLIST
          .map(i -> struct(TestType.EXPR, v, o, i))));

  private final Parser<CharSequence> P_LEFT =
      matcher(characters('{')).prefix(P_SPACE);

  private final Parser<CharSequence> P_RIGHT =
      matcher(characters('}')).prefix(P_SPACE);

  private final Parser<Node<TestType>> P_BLOCK =
      P_EXPR.prefix(P_LEFT).suffix(P_RIGHT);

  private final Parser<Node<TestType>> P_CHOICE =
      matcher(literal("abc")).or(matcher(literal("def"))).or(matcher(literal("ghi"))).prefix(P_SPACE)
          .map(c -> atom(LITERAL, c));

  private final Parser<Node<TestType>> P_CHOICES =
      P_CHOICE.separated(P_SPACE)
          .map(c -> struct(CHOICES, c));

  enum TestType {
    CHOICES,
    EXPR,
    INTEGER,
    INTLIST,
    LITERAL,
    OP,
    VAR
  }

}
