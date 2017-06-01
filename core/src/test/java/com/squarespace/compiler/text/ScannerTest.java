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

import static com.squarespace.compiler.match.Recognizers.digits;
import static com.squarespace.compiler.match.Recognizers.literal;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;


public class ScannerTest {

  @Test
  public void testBasic() {
    Scanner scanner = new Scanner("a{b}c{d}");
    Scanner.Stream stm1 = scanner.stream();
    Scanner.Stream stm2 = scanner.stream();

    stm1.seek();
    stm2.seek();
    assertEquals(stm1.toString(), "{b}c{d}");
    assertEquals(stm1, stm2);
    assertTrue(stm1.equalsCharacters("{b}c{d}", 0, 7));
    assertFalse(stm1.equalsCharacters("{c}c{c}", 0, 7));
    assertFalse(stm1.equalsCharacters("a{b}c{d}", 0, 8));

    stm2.seek();
    assertEquals(stm2.peek(), 'b');

    stm2.setFrom(stm1);
    assertEquals(stm1, stm2);
  }

  @Test
  public void testSeekBounds() {
    Scanner scanner = new Scanner("a{b}c{d}");
    Scanner.Stream stm1 = scanner.stream();
    Scanner.Stream stm2 = scanner.stream();

    boolean found = stm1.seekBounds(stm2, '{', '}');
    assertTrue(found);
    assertEquals(stm1.toString(), "c{d}");
    assertEquals(stm2.toString(), "{b}");

    scanner = new Scanner("abc{def{ghi}");
    stm1 = scanner.stream();
    stm2 = scanner.stream();

    found = stm1.seekBounds(stm2, '{', '}');
    assertFalse(found);
    assertEquals(stm1.toString(), "");
    assertEquals(stm2.toString(), "abc{def{ghi}");
  }

  @Test
  public void testSkipWs() {
    Scanner scanner = new Scanner("\t\n\r abc");
    Scanner.Stream stm = scanner.stream();

    stm.skipWs();
    assertEquals(stm.toString(), "abc");

    stm = new Scanner(" \t\n\r ").stream();
    stm.skipWs();
    assertEquals(stm.toString(), "");
    assertEquals(stm.peek(), Chars.EOF);
  }

  @Test
  public void testSeekRecognizer() {
    Scanner scanner = new Scanner("abc123");
    Scanner.Stream stm1 = scanner.stream();
    Scanner.Stream stm2 = scanner.stream();

    stm1.seek(digits(), stm2);
    assertEquals(stm2.toString(), "abc123");

    stm1.seek(literal("abc"), stm2);
    assertEquals(stm2.toString(), "abc");

    stm1.jump(stm2);
    stm1.seek(digits(), stm2);
    assertEquals(stm2.toString(), "123");
  }
}
