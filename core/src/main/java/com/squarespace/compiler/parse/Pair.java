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


public class Pair<T, S> {

  public final T _1;

  public final S _2;

  private Pair(T _1, S _2) {
      this._1 = _1;
      this._2 = _2;
  }

  public static <T, S> Pair<T, S> pair(T k, S v) {
      return new Pair<>(k, v);
  }

}
