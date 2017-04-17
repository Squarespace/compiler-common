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

package com.squarespace.compiler.text;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.annotations.Test;


public class EncodeUtilsTest {

  private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";

  private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

  private static final String DIGIT = "0123456789";

  private static final String ESCAPE_SYMBOLS = "@*_+-./";

  private static final String URI_MARK = "-_.!~*'()";

  private static final String URI_RESERVED = ";/?:@&=+$,";

  private static final String URI_ALPHA = LOWER + UPPER;

  private static final String URI_UNESCAPED = URI_ALPHA + DIGIT + URI_MARK;

  private static final Pattern HEXESC2 = Pattern.compile("%[0-9a-fA-F]{2}");

  private static final Pattern HEXESC4 = Pattern.compile("%u[0-9a-fA-F]{4}");

  private static final Pattern HEXESC2_ALL = Pattern.compile("^(%[0-9a-fA-F]{2})+$");

  private static final Pattern HEXESC4_ALL = Pattern.compile("^(%u[0-9a-fA-F]{4})+$");

  @Test
  public void testEncodeURI() {
      String ignored = URI_RESERVED + URI_UNESCAPED + "#";
      testEscapeFunction(ignored, new Escaper() {
        public String escape(String s) {
          return EncodeUtils.encodeURI(s);
        }
      });
  }

  @Test
  public void testEncodeURIComponent() {
    String ignored = URI_UNESCAPED;
    testEscapeFunction(ignored, new Escaper() {
      @Override
      public String escape(String s) {
        return EncodeUtils.encodeURIComponent(s);
      }
    });
  }

  @Test
  public void testEscape() {
    String ignored = URI_ALPHA + DIGIT + ESCAPE_SYMBOLS;
    testEscapeFunction(ignored, new Escaper() {
      @Override
      public String escape(String s) {
        return EncodeUtils.escape(s);
      }
    });
  }

  private void testEscapeFunction(String ignored, Escaper impl) {
    // Test all characters that we never escape
    String result = impl.escape(ignored);
    assertEquals(result, ignored);

    // Test all 2-character hex escape sequences
    String input = charsExcept(ignored, (char)0xff);
    result = impl.escape(input);

    // Ensure result contains only 2-character escapes
    assertTrue(HEXESC2_ALL.matcher(result).matches(), result);

    // Ensure we've encoded the correct number of characters
    int matches = countMatches(HEXESC2.matcher(result));
    assertEquals(matches, input.length());

    // Test all 4-character hex escape sequences
    input = charRange((char)0x100, (char)0xfffe);
    result = impl.escape(input);

    // Ensure result contains only 4-character escapes
    assertTrue(HEXESC4_ALL.matcher(result).matches());

    // Ensure we've encoded the correct number of characters
    matches = countMatches(HEXESC4.matcher(result));
    assertEquals(matches, input.length());
  }

  /**
   * Count number of matches for the given regex.
   */
  private static int countMatches(Matcher m) {
    int count = 0;
    while (m.find()) {
      count++;
    }
    return count;
  }

  /**
   * Build a string containing all characters up to 'end' except
   * those in the given string given.
   */
  private static String charsExcept(String except, char end) {
    Set<Character> ignore = new HashSet<>();
    for (int i = 0; i < except.length(); i++) {
      ignore.add(except.charAt(i));
    }
    StringBuilder b = new StringBuilder();
    for (char c = (char)0; c < end; c++) {
      if (!ignore.contains(c)) {
        b.append(c);
      }
    }
    return b.toString();
  }

  /**
   * Build a string containing all characters from 'start' up to 'end'.
   */
  private static String charRange(char start, char end) {
    StringBuilder b = new StringBuilder();
    for (char c = start; c <= end; c++) {
      b.append(c);
    }
    return b.toString();
  }

  private interface Escaper {
    String escape(String s);
  }
}
