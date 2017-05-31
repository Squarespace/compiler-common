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

import org.testng.annotations.Test;


public class ScannerTest {

  @Test
  public void testBasic() {
    Scanner scanner = new Scanner("a{b}c{d}");
    Scanner.Stream stm1 = scanner.stream();
    stm1.seek();
    Scanner.Stream stm2 = scanner.stream();
    stm2.seek();
    assertEquals(stm1, stm2);
  }

  @Test
  public void testSeekBounds() {
    Scanner scanner = new Scanner("a{b}c{d}");
    Scanner.Stream stm1 = scanner.stream();
    Scanner.Stream stm2 = scanner.stream();

    stm1.seekBounds(stm2, '{', '}');
    assertEquals(stm1.toString(), "c{d}");
    assertEquals(stm2.toString(), "{b}");
  }

}
