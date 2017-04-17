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


import static com.squarespace.compiler.text.CharClass.DIGIT;
import static com.squarespace.compiler.text.CharClass.ESCAPE_SYMS;
import static com.squarespace.compiler.text.CharClass.LOWERCASE;
import static com.squarespace.compiler.text.CharClass.NUMBER_SIGN;
import static com.squarespace.compiler.text.CharClass.UPPERCASE;
import static com.squarespace.compiler.text.CharClass.URI_MARK;
import static com.squarespace.compiler.text.CharClass.URI_RESERVED;
import static com.squarespace.compiler.text.CharClass.isMember;
import static com.squarespace.compiler.text.Chars.hexchar;


/**
 * Implements the encodeURI, encodeURIComponent and escape functions, which are available
 * as built-ins for JavaScript, but which no efficient existing implementation could
 * be found for Java at the time of authorship. This is a straightforward implementation
 * following recent versions of the V8 JavaScript engine.
 *
 * Synopsis of escaping rules from ECMA-262 below. Each function escapes all characters
 * except those on the right-hand side:
 *
 *    escape()             - escapeSpecial
 *    encodeURI()          - uriReserved | uriUnescaped | '#'
 *    encodeURIComponent() - uriUnescaped
 *
 * Where:
 *
 *   escapeSpecial:   uriAlpha | decimalDigit | escapeSymbols
 *   escapeSymbols:   * _ + - . /
 *     uriReserved:   ; / ? : @ & = + $ ,
 *   uriUnescaped:   uriAlpha | decimalDigit | uriMark
 *       uriAlpha:   lower | upper
 *          lower:   abcdefghijklmnopqrstuvwxyz
 *          upper:   ABCDEFGHIJKLMNOPQRSTUVWXYZ
 *   decimalDigit:   0-9
 *        uriMark:   - _ . ! ~ * ' ( )
 */
public class EncodeUtils {

  /**
   * Tests character membership for {@link #encodeURI(String)} per ECMA-262:
   * https://www.ecma-international.org/ecma-262/7.0/index.html#sec-encodeuri-uri
   */
  private static final CharPredicate ENCODE_URI = new CharPredicate() {
    @Override
    public boolean member(char ch) {
      return isMember(ch, LOWERCASE | UPPERCASE | DIGIT | NUMBER_SIGN | URI_RESERVED | URI_MARK);
    }
  };

  /**
   * Tests character membership for {@link #encodeURIComponent(String)} per ECMA-262
   * https://www.ecma-international.org/ecma-262/7.0/index.html#sec-encodeuricomponent-uricomponent
   */
  private static final CharPredicate ENCODE_URI_COMPONENT = new CharPredicate() {
    @Override
    public boolean member(char ch) {
      return isMember(ch, LOWERCASE | UPPERCASE | DIGIT | URI_MARK);
    }
  };

  /**
   * Tests character membership for {@link #escape(String)}
   */
  private static final CharPredicate ESCAPE = new CharPredicate() {
    @Override
    public boolean member(char ch) {
      return isMember(ch, LOWERCASE | UPPERCASE | DIGIT | ESCAPE_SYMS);
    }
  };

  private EncodeUtils() {
  }

  /**
   * Interface for testing a character's membership in a class.
   */
  private interface CharPredicate {
    boolean member(char ch);
  }

  /**
   * Implementation of JavaScript's encodeURI function.
   */
  public static String encodeURI(String uri) {
    return encodeChars(uri, ENCODE_URI);
  }

  /**
   * Implementation of JavaScript's encodeURIComponent function.
   */
  public static String encodeURIComponent(String uri) {
    return encodeChars(uri, ENCODE_URI_COMPONENT);
  }

  /**
   * Extends encodeURI with further character escapes.
   */
  public static String escape(String uri) {
    return encodeChars(uri, ESCAPE);
  }

  /**
   * Encodes the given string, passing through characters for which the CharPredicate
   * evaluates to true, and hex-escaping the rest. Escaped sequences are 2-byte %AB
   * and 4-byte %uABCD encodings.
   *
   * This follows a more recent version of V8 JavaScript engine, introduced in tagged
   * version 5.3.100.
   */
  private static String encodeChars(String uri, CharPredicate predicate) {
    StringBuilder buf = new StringBuilder();
    int size = uri.length();
    int i = 0;
    while (i < size) {
      char ch = uri.charAt(i);
      if (ch >= 256) {
        buf.append("%u");
        buf.append(hexchar(ch >> 12));
        buf.append(hexchar((ch >> 8) & 0xf));
        buf.append(hexchar((ch >> 4) & 0xf));
        buf.append(hexchar(ch & 0xf));

      } else if (predicate.member(ch)) {
        buf.append(ch);

      } else {
        buf.append('%');
        buf.append(hexchar(ch >> 4));
        buf.append(hexchar(ch & 0xf));
      }
      i++;
    }
    return buf.toString();
  }

}
